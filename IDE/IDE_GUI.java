import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class IDE_GUI extends JFrame {

    JavaFile Jfile;
    JTextField Open_textfield = new JTextField(20);
    JTextField Save_textfield = new JTextField(20);
    JTextArea Editing_window_textarea = new JTextArea(20, 50);
    JTextArea Result_window_textarea = new JTextArea(20, 50);

    public IDE_GUI() {
        // open, save, editing window, compile, save errors, delete, clear를 gui로 만듦
        setTitle("gui");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container c = getContentPane();
        setLayout(new FlowLayout());

        JButton open_button = new JButton("open");
        JButton save_button = new JButton("save");
        JButton compile_button = new JButton("compile");
        JButton save_error_button = new JButton("save_error");
        JButton delete_button = new JButton("delete");
        JButton clear_button = new JButton("clear");


        compile_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String path = Open_textfield.getText();
                Jfile = upload_file(path);
                compile_JavaFile(Jfile);
            }
        });

        save_error_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    save_error();
                } catch (IOException ex) {

                }
            }
        });

        delete_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String path = Open_textfield.getText();
                Jfile = upload_file(path);
                Jfile.delete_javaFile(Result_window_textarea);

            }
        });

        clear_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            clear();
            }
        });

        c.add(Open_textfield);
        c.add(open_button);
        c.add(Save_textfield);
        c.add(save_button);
        c.add(new JScrollPane(Editing_window_textarea));
        c.add(compile_button);
        c.add(save_error_button);
        c.add(delete_button);
        c.add(clear_button);
        c.add(Result_window_textarea);

        setSize(400, 600);
        setVisible(true);

    }
        JavaFile upload_file (String S) {
            String path;
            path = S;
            return new JavaFile(path);
        }

        boolean compile_JavaFile (JavaFile file1){
         String path = Open_textfield.getText();
         Jfile = new JavaFile(path);
         File file = new File(path);
         Jfile.javaFile = new File(file.getPath());

            if (file == null) {
                Result_window_textarea.append("We Do Not Have ANY Java File!");
                Result_window_textarea.append("please upload your Java file");
                return false;
            }
            try {
                //에러 메세지를 저장할 파일 생성
                Jfile.ErrorFile = new File(file.getPath() + ".error");
                ProcessBuilder compile = new ProcessBuilder("javac", file.getPath()); //.class

                Process compileProcess = compile.start();
                int exit_code = compileProcess.waitFor();

                if (exit_code == 0) {
                    Result_window_textarea.setText("");
                    Result_window_textarea.append("“compiled successfully…\n");
                    //Jfile.DeleteErrFile();
                    return true;
                } else {
                    Result_window_textarea.setText("");
                    java_FileErrorOutput(compileProcess);
                    return false;
                }
            //C:\Users\SeaDo\Desktop\Test.java
            } catch (Exception e) {

                Result_window_textarea.setText("");
                Result_window_textarea.append("UnExpected Error Occured while compiling your JavaFile!!\n");
                return false;
            }
        }

        void save_error () throws IOException {
            String path = Open_textfield.getText();
            String error = Result_window_textarea.getText();

            Jfile = new JavaFile(path);
            File file = new File(path);
            boolean compile_check = compile_JavaFile(Jfile);

            Jfile.ErrorFile = new File(file.getPath() + ".error");
            //만약 에러 메세지 파일이 없으면 생성한다.
            if (!compile_check) {
                if (!Jfile.ErrorFile.exists()) {
                    Jfile.ErrorFile.createNewFile();
                }
                try{
                    BufferedWriter writer = new BufferedWriter(new FileWriter(Jfile.ErrorFile));
                    writer.write(error);
                    writer.close();
            }
                catch (IOException ex) {

                }
            }
        }

        void clear(){ //전부 초기화하는 함수
        Open_textfield.setText("");
        Save_textfield.setText("");
        Editing_window_textarea.setText("");
        Result_window_textarea.setText("");
        Jfile = null;

        }

    void java_FileErrorOutput (Process process) throws IOException { //에러를 textarea에 출력하는 함수
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        {
            String s;
            while ((s = reader.readLine()) != null) {
                Result_window_textarea.append(s+"\n");
            }
        }
    }

    }

    class JavaFile {
        String path;
        File ErrorFile;
        File javaFile;

        JavaFile(String S) {
            path = S;
            javaFile = new File(S);
        }

        public void DeleteErrFile() { //에러파일을 삭제하는 함수
            System.out.println(ErrorFile);
            if (ErrorFile == null) {
                System.out.println("There is no ErrorFile");
                return;
            }
            if (!ErrorFile.exists()) {
                System.out.println("There is no ErrorFile");
                return;
            }
            try {
                ErrorFile.delete();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        void delete_javaFile (JTextArea textArea) {//자바 파일을 삭제하는 함수
            if (javaFile == null) {
                textArea.setText("");
                textArea.append("java file not found");
                return;
            }
            if (!javaFile.exists()) {
                textArea.setText("");
                textArea.append("java file not found");
                return;
            }
            try {
                javaFile.delete();
                textArea.setText("");
                textArea.append("java file deleted");
            } catch (Exception e) {
                textArea.append(e.getMessage());
            }
        }

    public static void main(String[] args) {
        new IDE_GUI();
    }

}

