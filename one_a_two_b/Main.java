package one_a_two_b;

import java.awt.*;
import java.awt.event.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

class MessageBox extends Dialog
{
    private final Button ok;
    private final Font font;
    private String message;

    public MessageBox(Frame parent, String title)
    {
        super(parent, title, true);
        ok = new Button("確定");
        font = new Font("新細明體", Font.PLAIN, 16);
        setUp();
    }

    private void setUp()
    {
        this.setSize(280, 200);
        this.addWindowListener(new DialogAdapter());
        this.setLayout(null);

        ok.setFont(new Font("新細明體", Font.PLAIN, 16));
        ok.setBounds(75, 150, 75, 25);
        ok.addActionListener(new DialogListener());

        this.add(ok);
    }

    public void display(String set_message)
    {
        message = set_message;
        this.setVisible(true);
    }

    private class DialogListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            MessageBox.this.dispose();
        }
    }

    private class DialogAdapter extends WindowAdapter
    {
        @Override
        public void windowClosing(WindowEvent windowEvent)
        {
            MessageBox.this.dispose();
        }
    }

    @Override
    public void paint(Graphics graphics)
    {
        graphics.setFont(font);
        graphics.drawString(message, 25,100);
    }
}

class Game
{
    private final Frame frame;
    private final Label[] rules;
    private final TextField number;
    private final Label answer_label;
    private final List record_list;
    private final Button check;
    private final Button reset;
    private final MessageBox error;

    private final int[] answer;
    private final int[] compare_array;
    private final int[] compare_answer;
    private final String regular_number;

    private Instant begin;
    private int guess_time;

    private static final byte ANS_NUM = 4; //題目的數字量 不可超過10個

    public Game()
    {
        frame = new Frame("1A2B");
        rules = new Label[3];
        rules[0] = new Label("規則:");
        rules[1] = new Label(ANS_NUM + "位不重複數字");
        rules[2] = new Label("0可為首位數字");
        number = new TextField(ANS_NUM);
        answer_label = new Label("0 A 0 B");
        record_list = new List();
        check = new Button("確定");
        reset = new Button("重置");
        error = new MessageBox(frame, "輸入錯誤");

        answer = new int[ANS_NUM];
        compare_array = new int[10];
        compare_answer = new int[10];
        regular_number = "\\d".repeat(ANS_NUM);
        guess_time = 0;
    }

    private void resetArray(int[] target_array)
    {
        for (int i = 0; i < 10; i++)
            target_array[i] = i;
    }

    private void generateAnswer()
    {
        int[] shuffle = new int[10]; //宣告一個陣列
        resetArray(shuffle); //將其初始化為0~9
        Random random = new Random();

        //將shuffle洗牌
        for (int i = 0, temp, rd_index; i < 9; i++) //陣列只需走訪到8 因為shuffle[9]沒有後面的數字可以隨機交換了
        {
            rd_index = random.nextInt(10 - i) + i; //將每個數字與它之後的數字隨機交換

            //交換shuffle[i]以及shuffle[rd_index]
            temp = shuffle[rd_index];
            shuffle[rd_index] = shuffle[i];
            shuffle[i] = temp;
        }

        //取出洗牌後的shuffle前ANS_NUM項
        resetArray(compare_answer); //恢復比較答案的陣列
        for (int i = 0; i < ANS_NUM; i++)
        {
            answer[i] = shuffle[i]; //取出前ANS_NUM項
            compare_answer[answer[i]] = -1; //標註為-1 之後檢查答案會用到
        }
    }

    public void run()
    {
        generateAnswer(); //產生答案並記錄到compare_answer陣列上

        frame.setSize(640, 480);
        frame.addWindowListener(new MainAdapter());
        frame.setLayout(null);

        for (int i = 0; i < 3; i++)
        {
            rules[i].setForeground(Color.black);
            rules[i].setBounds(30, 60 + (i << 5), 140, 24);
            rules[i].setFont(new Font("新細明體", Font.PLAIN, 20));
            frame.add(rules[i]);
        }

        number.addKeyListener(new TextListener());
        number.setBounds(200, 120, 200, 72);
        number.setFont(new Font("新細明體", Font.PLAIN, 36));

        answer_label.setBounds(220, 240, 240, 100);
        answer_label.setFont(new Font("新細明體", Font.PLAIN, 48));

        record_list.setBounds(480, 80, 140, 360);
        record_list.setFont(new Font("新細明體", Font.PLAIN, 16));

        check.addActionListener(new CheckListener());
        check.setBounds(180, 340, 240, 80);
        check.setFont(new Font("新細明體", Font.PLAIN, 24));

        reset.addActionListener(new ResetListener());
        reset.setBounds(30, 350, 80, 60);
        reset.setFont(new Font("新細明體", Font.PLAIN, 16));

        begin = Instant.now(); //開始計時

        frame.add(number);
        frame.add(answer_label);
        frame.add(record_list);
        frame.add(check);
        frame.setVisible(true);
    }

    private void checkAnswer()
    {
        String enter_answer_str = number.getText(); //輸入的字串
        if (!enter_answer_str.matches(regular_number)) //如果不是ANS_NUM個數字
        {
            error.display("請輸入 " + ANS_NUM + " 個0 ~ 9的數字");
            return;
        }

        int[] enter_answer_int = new int[ANS_NUM]; //準備陣列 等一下要把字串轉換成陣列
        for (int i = 0; i < ANS_NUM; i++)
            enter_answer_int[i] = Character.getNumericValue(enter_answer_str.charAt(i)); //將char轉換成int 並將數字放進陣列

        resetArray(compare_array); //重置比較陣列為0 ~ 9
        for (int i = 0; i < ANS_NUM; i++) //檢查輸入答案是否重複
        {
            if (compare_array[enter_answer_int[i]] != -1)
                compare_array[enter_answer_int[i]] = -1;
            else
            {
                error.display("請勿輸入重複的數字");
                return; //表示有數字重複
            }
        }

        int A = 0, B = 0;
        for (int i = 0; i < ANS_NUM; i++)
        {
            if (compare_answer[enter_answer_int[i]] == -1) //代表產生答案並放入陣列時 有放到這個數字
            {
                if (enter_answer_int[i] == answer[i]) //代表現在在檢查的這個數字 跟答案的位置一樣
                    A++;
                else //雖然已確認有這個數字 但位置與答案不同
                    B++;
            }
        }
        answer_label.setText(A + " A " + B + " B");
        record_list.add(enter_answer_str + ": " + A + " A " + B + " B");
        guess_time++; //猜測次數 + 1
        if (A == 4) //遊戲結束
        {
            Instant end = Instant.now(); //計時結束
            Duration game_time = Duration.between(begin, end);
            long second = game_time.toSeconds();
            record_list.add("用時: " + second / 60 + "分" + second % 60 + "秒");
            record_list.add("猜測次數: " + guess_time + "次");
            frame.add(reset); //放上重置
        }
    }

    //按下檢查按鈕 但其實大家都按Enter
    private class CheckListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            checkAnswer();
        }
    }

    //按Enter
    private class TextListener implements KeyListener
    {
        @Override
        public void keyTyped(KeyEvent event)
        {}

        @Override
        public void keyPressed(KeyEvent event)
        {
            if (event.getKeyCode() == KeyEvent.VK_ENTER)
                checkAnswer();
        }

        @Override
        public void keyReleased(KeyEvent event)
        {}
    }

    //按重置
    private class ResetListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            generateAnswer();
            number.setText("");
            answer_label.setText("0 A 0 B");
            record_list.removeAll();
            begin = Instant.now();
            guess_time = 0;
            frame.remove(reset);
        }
    }

    //按X
    private class MainAdapter extends WindowAdapter
    {
        @Override
        public void windowClosing(WindowEvent windowEvent)
        {
            frame.dispose();
            System.exit(0);
        }
    }
}

public class Main
{
    public static void main(String[] args)
    {
        Game game = new Game();
        game.run();
    }
}