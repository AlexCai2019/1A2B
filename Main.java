package one_a_two_b;

import java.awt.*;
import java.awt.event.*;
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
        message = "";
        setUp();
    }

    private void setUp()
    {
        this.setSize(300, 200);
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
        public void windowClosing(WindowEvent event)
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

    private final int ans_num = 4; //題目的數字量 不可超過10個

    public Game()
    {
        frame = new Frame("1A2B");
        rules = new Label[3];
        rules[0] = new Label("規則:");
        rules[1] = new Label(ans_num + "位不重複數字");
        rules[2] = new Label("0可為首位數字");
        number = new TextField(6);
        answer_label = new Label("0 A 0 B");
        record_list = new List();
        check = new Button("確定");
        reset = new Button("重置");
        error = new MessageBox(frame, "輸入錯誤");

        answer = new int[ans_num];
        compare_array = new int[10];
    }

    private void resetCompareArray()
    {
        for (int i = 0; i < 10; i++)
            compare_array[i] = i;
    }

    private void generateAnswer()
    {
        Random random = new Random();

        for (int i = 0; i < ans_num; )
        {
            int rd = random.nextInt(10); //產生0 ~ 9 的隨機數
            if (compare_array[rd] != -1) //如果沒遇過這個隨機數字
            {
                answer[i] = rd; //設定這個數字
                compare_array[rd] = -1; //標記已經遇過
                i++; //下一個
            }
        }
    }

    public void run()
    {
        resetCompareArray();
        generateAnswer();

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

        //number.setForeground(Color.black);
        number.addKeyListener(new textListener());
        number.setBounds(200, 120, 200, 72);
        number.setFont(new Font("新細明體", Font.PLAIN, 36));

        //answer_label.setForeground(Color.black);
        answer_label.setBounds(220, 240, 240, 100);
        answer_label.setFont(new Font("新細明體", Font.PLAIN, 48));

        //record_list.setForeground(Color.black);
        record_list.setBounds(480, 80, 140, 360);
        record_list.setFont(new Font("新細明體", Font.PLAIN, 16));

        check.addActionListener(new checkListener());
        check.setBounds(180, 340, 240, 80);
        check.setFont(new Font("新細明體", Font.PLAIN, 24));

        reset.addActionListener(new resetListener());
        reset.setBounds(30, 350, 80, 60);
        reset.setFont(new Font("新細明體", Font.PLAIN, 16));


        frame.add(number);
        frame.add(answer_label);
        frame.add(record_list);
        frame.add(check);
        frame.setVisible(true);
    }

    private void checkAnswer()
    {
        String enter_answer_str = number.getText(); //輸入的字串
        if (enter_answer_str.length() != ans_num) //如果大小不是設定的大小
        {
            error.display("請輸入" + ans_num + "個數字");
            return;
        }

        int[] enter_answer_int = new int[ans_num]; //準備陣列 等一下要把字串轉換成陣列

        for (int i = 0; i < ans_num; i++)
        {
            char convert = enter_answer_str.charAt(i);
            if (Character.isDigit(convert))
                enter_answer_int[i] = Character.getNumericValue(convert); //將char轉換成int 並將數字放進陣列
            else
            {
                error.display("請勿輸入0 ~ 9的數字以外的內容");
                return;
            }
        }

        resetCompareArray(); //重置比較陣列為0 ~ 9
        for (int i = 0; i < ans_num; i++) //檢查輸入答案是否重複
        {
            if (compare_array[enter_answer_int[i]] != -1)
                compare_array[enter_answer_int[i]] = -1;
            else
            {
                error.display("請勿輸入重複的數字");
                return; //表示有數字重複
            }
        }

        resetCompareArray(); //重置比較陣列為0 ~ 9
        for (int i = 0; i < ans_num; i++)
            compare_array[answer[i]] = -1; //將正確答案放進比較陣列中

        int A = 0, B = 0;
        for (int i = 0, check_num; i < ans_num; i++)
        {
            check_num = enter_answer_int[i]; //現在在檢查的這個數字
            if (compare_array[check_num] == -1) //代表剛才放入答案時 有放到這個數字
            {
                if (enter_answer_int[i] == answer[i]) //代表現在在檢查的這個數字 跟答案的位置一樣
                    A++;
                else //雖然已確認有這個數字 但答案不同
                    B++;
            }
        }
        answer_label.setText(A + " A " + B + " B");
        record_list.add(enter_answer_str + ": " + A + " A " + B + " B");
        if (A == 4)
            frame.add(reset);
    }

    private class checkListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            checkAnswer();
        }
    }

    private class textListener implements KeyListener
    {
        @Override
        public void keyTyped(KeyEvent e)
        {}

        @Override
        public void keyPressed(KeyEvent event)
        {
            if (event.getKeyCode() == KeyEvent.VK_ENTER)
                checkAnswer();
        }

        @Override
        public void keyReleased(KeyEvent e)
        {}
    }

    private class resetListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            resetCompareArray();
            generateAnswer();
            number.setText("");
            answer_label.setText("0 A 0 B");
            record_list.removeAll();
            frame.remove(reset);
        }
    }

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
