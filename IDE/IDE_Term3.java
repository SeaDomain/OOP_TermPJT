import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;

public class IDE_Term3{
    public static void main(String[] args) {
        new UI();
    }
}

class UI extends JFrame{
    //컴파일/실행 결과창
    private JTextArea T_result;
    //메뉴 바
    private JMenuBar MenuBar;
    private JMenuItem open, close, save, saveas, quit, compile, Run;
    //에디터 화면
    private JTabbedPane T_edit = new JTabbedPane();

    UI(){
        setTitle("IDE3.0");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container c = getContentPane();
        setContainer(c);

        setSize(950, 900);
        setVisible(true);
    }

    //메인 화면 세팅 메소드
    void setContainer(Container c){
        c.setLayout(new BorderLayout());

        //T_edit.addTab("new", CreateArea());
        //ADD Event Listener at HERE

        T_result = new JTextArea();   //TextArea For printing Results
        T_result.setMargin(new Insets(0, 10, 0, 0));
        //ADD Event Listener at HERE

        JSplitPane spliter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        spliter.setDividerLocation(600);

        SetMenu();

        spliter.setTopComponent(T_edit);
        spliter.setBottomComponent(new JScrollPane(T_result));
        c.add(spliter);
        setJMenuBar(MenuBar);
    }

    //메뉴바 세팅 메소드
    void SetMenu(){
        MenuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu run = new JMenu("Run");

        open = new JMenuItem("Open");
        close = new JMenuItem("Close");
        save = new JMenuItem("Save");
        saveas = new JMenuItem("Save As");
        quit = new JMenuItem("Quit");
        compile = new JMenuItem("Compile");
        Run = new JMenuItem("Run");

        open.addActionListener(new OpenEvent());
        close.addActionListener(new CloseEvent());
        save.addActionListener(new SaveEvent());
        saveas.addActionListener(new SaveAsEvent());
        quit.addActionListener(new QuitEvent());
        compile.addActionListener(new CompileEvent());
        compile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK)); // CTRL+R 단축키 함수
        Run.addActionListener(new RunEvent());


        file.add(open);
        file.add(close);
        file.add(save);
        file.add(saveas);
        file.add(quit);

        run.add(compile);
        run.add(Run);

        MenuBar.add(file);
        MenuBar.add(run);
    }

    //에디터 화면 생성 메소드
    JComponent CreateArea(){
        JTextArea Area = new JTextArea(45, 30);     //Main Editing Area
        return new JScrollPane(Area);
    }

    //EVENT HANDLER
    class OpenEvent implements ActionListener{
        public void actionPerformed(ActionEvent e){
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("java FIle", "java");
            chooser.setFileFilter(filter);

            int result = chooser.showOpenDialog(null);
            if(result == JFileChooser.APPROVE_OPTION){
                File selected = chooser.getSelectedFile();
                if(selected.exists())
                    T_edit.addTab(selected.getName(), new JScrollPane(new JAVAFile(selected)));
                else{
                    JOptionPane.showMessageDialog(null, "ERROR!!! There is No such File(" + selected.getPath() + ")", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    class CloseEvent implements ActionListener{
        public void actionPerformed(ActionEvent e){
            T_edit.remove(T_edit.getSelectedComponent());
        }
    }

    class SaveEvent implements ActionListener{
        public void actionPerformed(ActionEvent e){
            int result = JOptionPane.showConfirmDialog(getContentPane(), "Do you want to Save?", "Save Checking", JOptionPane.YES_NO_OPTION);

            if(result == JOptionPane.YES_OPTION){
                JScrollPane scroll = (JScrollPane)T_edit.getSelectedComponent();
                JAVAFile current = (JAVAFile)scroll.getViewport().getView();
                current.save();
            }
            else{
            }
        }
    }

    class SaveAsEvent implements ActionListener{
        public void actionPerformed(ActionEvent e){
            JScrollPane scroll = (JScrollPane)T_edit.getSelectedComponent();
            JAVAFile current = (JAVAFile)scroll.getViewport().getView();

            String name = JOptionPane.showInputDialog(getContentPane(), "Name your File:", current.path);
            if(name != null){
                current.save(name);
                T_edit.setTitleAt(T_edit.getSelectedIndex(), current.JavaFile.getName());
            }
        }
    }

    class QuitEvent implements ActionListener{
        public void actionPerformed(ActionEvent e){
            System.exit(0);
        }
    }

    class CompileEvent implements ActionListener{
        public void actionPerformed(ActionEvent e){
            JScrollPane scroll = (JScrollPane)T_edit.getSelectedComponent();
            JAVAFile current = (JAVAFile)scroll.getViewport().getView();

            current.compile(T_result);
        }
    }
    class CompilekeyEvent extends KeyAdapter { // key Listener 함수
        public void keyPressed(KeyEvent e){
            JScrollPane scroll = (JScrollPane)T_edit.getSelectedComponent();
            JAVAFile current = (JAVAFile)scroll.getViewport().getView();

            if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_R){
                current.compile(T_result);
            }
        }

    }
    class RunEvent implements ActionListener{
        public void actionPerformed(ActionEvent e){
            JScrollPane scroll = (JScrollPane)T_edit.getSelectedComponent();
            JAVAFile current = (JAVAFile)scroll.getViewport().getView();

            current.run(T_result);
        }
    }
}

//TextArea 상속.
class JAVAFile extends JTextArea{
    String path;
    File ErrorFile;
    File JavaFile;
    JAVAFile(File text){
        super(45, 30);
        setMargin(new Insets(0, 10, 0, 0));
        setFont(new Font("Consolas", Font.PLAIN, 15));

        JavaFile = text;
        path = text.getPath();
        open();
    }

    //자기 TextArea 에 출력.
    void open(){
        String s;
        try{
            BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(JavaFile)));
            while((s = read.readLine()) != null){
                append(s + "\n");
            }
            read.close();
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "ERROR!!! There was an Error at Reading From File", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Function for Saving text From TextArea
    void save(){
        try{
            BufferedWriter write = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(JavaFile)));
            for (String line : getText().split("\\n")){
                write.write(line + "\n");
            }
            write.flush();
            write.close();
        }
        catch(Exception e){
        }
    }
    //Function for SaveAs
    void save(String path){
        File temp = new File(path);
        if(temp.exists()){
            JOptionPane.showMessageDialog(this, "ERROR!!! File Already Exists!", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
        else{
            this.path = path;
            JavaFile = temp;
        }
        try{
            BufferedWriter write = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(JavaFile)));
            for (String line : getText().split("\\n")){
                write.write(line + "\n");
            }
            write.flush();
            write.close();
        }
        catch(Exception e){
        }
    }

    public boolean compile(JTextArea result){
        try{
            ProcessBuilder compile = new ProcessBuilder("javac", path);
            Process compileProcess = compile.start();
            int exit_code = compileProcess.waitFor();

            if (exit_code == 0) {
                result.setText("");
                result.append("compiled successfully…\n");
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

    public void run(JTextArea result){
        try{
            String java_class = JavaFile.getName().replace(".java", "");
            File Parent_Dir = JavaFile.getParentFile();

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