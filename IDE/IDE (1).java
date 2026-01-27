import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//main 객체
public class IDE {
    public static void main(String args[]) {
        new UI();
    }
}

class UI extends JFrame{
    //Window Components
    private JButton B_open, B_save, B_compile, B_saveErr, B_delete, B_clear, B_run;
    private JTextField T_open, T_save;
    private JTextArea T_edit, T_result;
    //Java Text File
    JavaFile Jfile;

    //initializer
    public UI(){
        setTitle("IDE2.0");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container c = getContentPane();
        setContainer(c);

        setSize(1050, 900);
        setVisible(true);
    }

    //Setting Graphic Layout
    void setContainer(Container c){
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

        JPanel open = new JPanel();
        open.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        T_open = new JTextField(30);
        T_open.setFont(new Font("Consolas", Font.PLAIN, 20));   //for TextField size
        B_open = new JButton("Open");
        open.add(T_open);
        open.add(B_open);
        //ADD Event Listener at HERE
        B_open.addActionListener(new OpenEvent());
        c.add(open);

        JPanel save = new JPanel();
        save.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        T_save = new JTextField(30);
        T_save.setFont(new Font("Consolas", Font.PLAIN, 20));   //for TextField size
        B_save = new JButton("Save");
        save.add(T_save);
        save.add(B_save);
        //ADD Event Listener at HERE
        B_save.addActionListener(new SaveEvent());
        c.add(save);

        T_edit = new JTextArea(45, 30);     //Main Editing Area
        T_edit.setMargin(new Insets(0, 10, 0, 0));
        T_edit.setFont(new Font("Consolas", Font.PLAIN, 15));
        //ADD Event Listener at HERE
        c.add(new JScrollPane(T_edit));

        JPanel Buttons = new JPanel();
        Buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 30));
        B_compile = new JButton("Compile");
        B_run = new JButton("Run");
        B_saveErr = new JButton("Save Errors");
        B_delete = new JButton("Delete");
        B_clear = new JButton("Clear");
        Buttons.add(B_compile);
        Buttons.add(B_run);
        Buttons.add(B_saveErr);
        Buttons.add(B_delete);
        Buttons.add(B_clear);
        //ADD Event Listener at HERE
        B_compile.addActionListener(new CompileEvent());
        B_run.addActionListener(new RunEvent());
        B_saveErr.addActionListener(new ErrSaveEvent());
        B_delete.addActionListener(new DeleteEvent());
        B_clear.addActionListener(new ClearEvent());
        c.add(Buttons);

        T_result = new JTextArea(25, 30);   //TextArea For printing Results
        T_result.setMargin(new Insets(0, 10, 0, 0));
        //ADD Event Listener at HERE
        c.add(new JScrollPane(T_result));
    }

    //Function for Printing Files at Textarea
    void print_at_textarea(File file, JTextArea t){
        String s;
        try{
            BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while((s = read.readLine()) != null){
                t.append(s + "\n");
            }
            read.close();
        }
        catch(Exception e){
            T_result.append("ERROR!!! There was an Error at Reading From File: " + file);
        }
    }
    //Function for Saving text From TextArea
    void save_at_file(File file, JTextArea t){
        try{
            BufferedWriter write = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            for (String line : t.getText().split("\\n")){
                write.write(line + "\n");
            }
            write.flush();
            write.close();
        }
        catch(Exception e){
            T_result.append("ERROR!!! There was an Error at Writing File: " + file);
        }
    }
    void clear(){ //전부 초기화하는 함수
        T_edit.setText("");
        T_result.setText("");
        T_save.setText("");
        T_open.setText("");
        Jfile = null;
    }

    //Event Listeners
    class OpenEvent implements ActionListener{
        public void actionPerformed(ActionEvent e){
            String s = T_open.getText();
            T_open.setText("");
            Jfile = new JavaFile(s);
            if(!Jfile.exists()){
                T_result.setText("");
                T_result.append("ERROR!!! There Is No Such File!");
                Jfile = null;
                return;
            }
            T_edit.setText("");
            print_at_textarea(Jfile, T_edit);
        }
    }
    class SaveEvent implements ActionListener{
        public void actionPerformed(ActionEvent e){
            String s = T_save.getText();

            //Case When Input Value is Empty
            if(s.equals("")){
                if(Jfile == null) {
                    T_result.setText("");
                    T_result.append("ERROR!!! Please Write Your File Name!!");
                }
                else{
                    save_at_file(Jfile, T_edit);
                }
            }
            //Case When Input Value Exists
            else{
                //case When JavaFile is Not yet Opened
                if(Jfile == null){
                    File newFile = new File(s);
                    if(newFile.exists()){
                        T_result.setText("");
                        T_result.append("ERROR!!! The File Named " + newFile + "is Already Exist!!!");
                        return;
                    }
                    else{
                        try{
                            T_result.setText("");
                            T_result.append("Saved File: " + newFile + "\n");
                            newFile.createNewFile();
                            save_at_file(newFile, T_edit);
                        }
                        catch(Exception ioE){
                            T_result.setText("");
                            T_result.append("ERROR!!! There Is Error At saving JavaFile: " + newFile);
                        }
                        Jfile = new JavaFile(s);
                    }
                }
                //Case When JavaFile is Opened
                else{
                    //Case When Input Value is JavaFile's path
                    if(s.equals(Jfile.getPath())){
                        save_at_file(Jfile, T_edit);
                    }
                    else{
                        File newFile = new File(s);
                        try{
                            newFile.createNewFile();
                            save_at_file(newFile, T_edit);
                        }
                        catch(Exception ioE){
                            T_result.setText("");
                            T_result.append("ERROR!!! There Is Error At saving JavaFile: " + newFile);
                        }
                        Jfile = new JavaFile(s);
                    }
                }
            }
        }
    }

    class CompileEvent implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(Jfile == null){
                T_result.setText("");
                T_result.append("ERROR!!! FILE NOT FOUND!!!");
                return ;
            }
            Jfile.compile(T_result);
        }
    }

    class ErrSaveEvent implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(Jfile == null){
                T_result.setText("");
                T_result.append("ERROR!!! FILE NOT FOUND!!!");
                return ;
            }
            Jfile.saveErr(T_result);
        }
    }
    class DeleteEvent implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(Jfile == null){
                T_result.setText("");
                T_result.append("ERROR!!! FILE NOT FOUND!!!");
                return ;
            }
            Jfile.delete(T_result);
            Jfile = null;
        }
    }
    class ClearEvent implements ActionListener{
        public void actionPerformed(ActionEvent e){
            clear();
        }
    }
    class RunEvent implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(Jfile == null){
                T_result.setText("");
                T_result.append("ERROR!!! FILE NOT FOUND!!!");
                return ;
            }
            Jfile.run(T_result);
        }
    }
}

class JavaFile extends File{
    String path;
    File ErrorFile;
    File JavaFile;

    JavaFile(String S) {
        super(S);
        path = S;
        ErrorFile = new File(S + ".error");
        JavaFile = new File(S);
    }

    public boolean compile(JTextArea result){
        try{
            ProcessBuilder compile = new ProcessBuilder("javac", path);
            Process compileProcess = compile.start();
            int exit_code = compileProcess.waitFor();

            if (exit_code == 0) {
                result.setText("");
                result.append("“compiled successfully…\n");
                return true;
            } else {
                result.setText("");
                result.read(new InputStreamReader(compileProcess.getErrorStream(), "MS949"), null);
                return false;
            }
        }
        catch(Exception e){
            result.setText("");
            result.append("ERROR!!! UNKNOWN ERROR AT Compiling.\n");
            return false;
        }
    }

    public void saveErr(JTextArea result){
        try{
            if(!compile(result)){
                if(!ErrorFile.exists()){
                    ErrorFile.createNewFile();
                    result.write(new FileWriter(ErrorFile));
                }
                else
                    result.write(new FileWriter(ErrorFile));
            }
            else {
                result.setText("");
                result.append("Not existing error !!");
            }
        }
        catch(Exception e){
            result.setText("");
            result.append("ERROR!!! Error at Saving Error Result\n");
        }
    }

    void delete (JTextArea textArea) {//자바 파일을 삭제하는 함수
        if (!JavaFile.exists()) {
            textArea.setText("");
            textArea.append("java file not existing");
            return;
        }
        try {
            JavaFile.delete();
            textArea.setText("");
            textArea.append("java file deleted");
        } catch (Exception e) {
            textArea.append(e.getMessage());
        }
    }

    public void run(JTextArea result){
        try{
            String java_class = getName().replace(".java", "");
            File Parent_Dir = getParentFile();

            ProcessBuilder run_class = new ProcessBuilder("java", java_class);
            run_class.directory(Parent_Dir);
            run_class.redirectErrorStream(true);
            Process run_process = run_class.start();

            result.setText("");
            result.read(new InputStreamReader(run_process.getInputStream(), "MS949"), null);
        }
        catch(Exception e){
            result.append("ERROR!!! Error at Running Javafile\n");
        }
    }
}