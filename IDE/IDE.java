import java.io.*;
import java.nio.file.Files;
import java.util.*;

//main 객체
public class IDE {
    public static void main(String args[]) {
        new CLI();
    }
}

//GUI 개발할 때를 대비하여 CLI 부분을 따로 떼놓음.
//기능을 추가하려면 loop()의 switch문에서 동작 코드를, print_menu()에서 기능및 번호 설명 코드를 추가할것
class CLI {
    JavaFile Jfile; // 업로드된 파일 정보를 저장할 변수

    //Constructor
    CLI() {
        loop();
    }

        //CLI loop
        void loop() {
            Scanner scan = new Scanner(System.in); // [수정] 스캐너는 한 번만 생성
        while (true) {
            print_menu();
            int action = scan.nextInt();
            //기능을 추가하고 싶으면 여기에 case를 추가해주세요
            switch (action) {
                case 1:
                    Scanner path = new Scanner(System.in);
                    Jfile = upload_file(path);
                    break;
                case 2:
                    if (Jfile == null) {
                        System.out.println("먼저 Java 파일을 업로드해주세요. (메뉴 1)");
                        break;
                    }
                    compileJavaFile(Jfile);
                    break;
                case 3:
                    // [추가] 파일이 업로드되었는지 확인
                    if (Jfile == null) {
                        System.out.println("먼저 Java 파일을 업로드해주세요. (메뉴 1)");
                        break;
                    }
                    runJavaFile(Jfile);
                    break;
                case 4:
                    reset_JavaFile();
                    break;
                case 5:
                    break;
                case 6:
                    System.out.println("GoodBye");
                    scan.close();
                    return;
                default:
                    System.out.println("We Do Not have your option.");
                    System.out.println("Please Try Again");
                    break;
            }
        }
    }

    void print_menu() {
        //기능을 추가하고 싶으면 여기에 기능 설명을 적어주세요.
        System.out.println("************************");
        System.out.println("JAVA TERM PROJECT");
        System.out.println("1. Java File Upload");
        System.out.println("2. Compile");
        System.out.println("3. Run");
        System.out.println("4. Reset");
        System.out.println("5. Check Error File");
        System.out.println("6. Exit");
        System.out.print(">");
    }

    JavaFile upload_file(Scanner scan) {
        String path;
        System.out.print("Java File Path:");
        path = scan.nextLine();
        return new JavaFile(path);
    }

    void compileJavaFile(JavaFile file) {
        try {
            File javaFile = new File(file.path);

            ProcessBuilder compile = new ProcessBuilder("javac", file.path);

            compile.redirectErrorStream(true); // 오류 스트림을 표준 출력 스트림으로 합침

            Process compileProcess = compile.start();

            printProcessOutput("컴파일", compileProcess);

            int exitCode = compileProcess.waitFor();

            if (exitCode == 0) {
                System.out.println("“compiled successfully …");
            } else {
                System.out.println("컴파일에 실패했습니다. (종료 코드: " + exitCode + ")");
            }

        } catch (IOException | InterruptedException e) { // [수정] InterruptedException 추가
            System.out.println("컴파일 중 오류 발생:");
            e.printStackTrace(); // [수정] 비어있는 catch 블록 대신 오류 로그 출력
        }
    }

    void runJavaFile(JavaFile file) {
        try {
            File javaFile = new File(file.path);
            File parentDir = javaFile.getParentFile(); // .java 파일이 위치한 디렉토리
            String className = javaFile.getName().replace(".java", "");
            File classFile = new File(parentDir, className + ".class");
//            if (!classFile.exists()) {
//                System.out.println(".class 파일이 존재하지 않습니다.");
//                System.out.println("먼저 컴파일을 실행해주세요. (메뉴 2)");
//                return;
//            }

            System.out.println(className + " 클래스를 실행합니다...");

            ProcessBuilder run = new ProcessBuilder("java", className);
            System.out.println(parentDir);
            run.directory(parentDir); // [중요] .class 파일이 있는 디렉토리에서 실행
            run.redirectErrorStream(true);

            Process runProcess = run.start();
            printProcessOutput("실행", runProcess);

            int exitCode = runProcess.waitFor();
            System.out.println("프로그램 종료 코드: " + exitCode);

        } catch (IOException | InterruptedException e) { // [수정] InterruptedException 추가
            System.out.println("실행 중 오류 발생:");
            e.printStackTrace();
        }
    }

    /**
     * 프로세스의 표준 출력/오류 스트림을 읽어 콘솔에 출력합니다.
     */
    void printProcessOutput(String tag, Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
{
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    void reset_JavaFile() {
        if (Jfile == null) {
            System.out.println("초기화할 파일 정보가 없습니다.");
            return;
        }

        String javaFilePath = Jfile.path;
        // .java 확장자를 .class로 변경하여 .class 파일 경로 생성
        String classFilePath = javaFilePath.substring(0, javaFilePath.lastIndexOf(".java")) + ".class";

        File classFile = new File(classFilePath);

        if (classFile.exists()) {
            if (classFile.delete()) {
                System.out.println(classFile.getName() + " 파일이 삭제되었습니다.");
            } else {
                System.out.println(classFile.getName() + " 파일 삭제에 실패했습니다.");
            }
        } else {
            System.out.println(".class 파일이 존재하지 않습니다. (이미 Reset됨)");
        }

        // Jfile 참조를 null로 변경하여 초기화
        Jfile = null;
        System.out.println("업로드된 파일 정보가 초기화되었습니다.");
    }
}

class JavaFile {
    String path;

    JavaFile(String S) {
        path = S;
    }
}