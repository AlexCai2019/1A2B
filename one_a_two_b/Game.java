package one_a_two_b;

import java.awt.*;
import java.awt.event.*;

public class Game
{
    private final Core core;

    private final Frame frame;
    private final TextField number;
    private final Label answer_label;
    private final List record_list;
    private final Button check;
    private final Button reset;
    private final MessageBox error;

    public static final String FONT_NAME = "Microsoft JhengHei";

    public Game()
    {
        core = new Core(); //遊戲核心

        //視窗本體
        frame = new Frame("1A2B");
        frame.setSize(640, 480);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent windowEvent)
            {
                frame.dispose();
                System.exit(0);
            }
        });
        frame.setLayout(null);
        frame.setVisible(true);

        //左上方規則
        final Label[] rules = new Label[3];
        rules[0] = new Label("規則:");
        rules[1] = new Label(Core.ANS_NUM + "位不重複數字");
        rules[2] = new Label("0可為首位數字");
        for (int i = 0; i < 3; i++)
        {
            rules[i].setForeground(Color.black);
            rules[i].setBounds(30, 60 + (i << 5), 140, 24);
            rules[i].setFont(new Font(FONT_NAME, Font.PLAIN, 20));
            frame.add(rules[i]);
        }

        //輸入框
        number = new TextField(Core.ANS_NUM);
        number.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent event) {}

            @Override
            public void keyPressed(KeyEvent event) //按Enter
            {
                if (event.getKeyCode() == KeyEvent.VK_ENTER)
                    checkAnswer();
            }

            @Override
            public void keyReleased(KeyEvent event) {}
        });
        number.setBounds(200, 120, 200, 72);
        number.setFont(new Font(FONT_NAME, Font.PLAIN, 36));
        frame.add(number);

        //顯示答案
        answer_label = new Label("0 A 0 B");
        answer_label.setBounds(220, 240, 200, 100);
        answer_label.setFont(new Font(FONT_NAME, Font.PLAIN, 48));
        frame.add(answer_label);

        //紀錄
        record_list = new List();
        record_list.setBounds(480, 80, 140, 360);
        record_list.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
        frame.add(record_list);

        //點擊確定
        check = new Button("確定");
        check.addActionListener((ActionEvent) -> checkAnswer()); //按下檢查按鈕 但其實大家都按Enter
        check.setBounds(180, 340, 240, 80);
        check.setFont(new Font(FONT_NAME, Font.PLAIN, 36));
        frame.add(check);

        //點擊重置
        reset = new Button("重置");
        reset.addActionListener((ActionListener) ->
        {
            core.reset();
            number.setText("");
            answer_label.setText("0 A 0 B");
            record_list.removeAll();
            frame.remove(reset);
        });
        reset.setBounds(30, 350, 80, 60);
        reset.setFont(new Font(FONT_NAME, Font.PLAIN, 16));

        //錯誤訊息
        error = new MessageBox(frame, "輸入錯誤");
    }

    private void checkAnswer()
    {
        String enter_answer_str = number.getText();
        CheckMessage check_message = core.checkAnswer(enter_answer_str);
        String answer_result = check_message.getResult();

        if (check_message.getCode()) //有錯誤或例外狀況
        {
            check.setEnabled(false); //禁用按鈕 其實沒有什麼用 只是效果
            error.display(answer_result);
            check.setEnabled(true); //解除按鈕
        }
        else
        {
            answer_label.setText(answer_result); //寫出幾A幾B
            record_list.add(enter_answer_str + ": " + answer_result); //記錄在旁邊的List
            if (answer_result.charAt(0) == Core.ANS_NUM + '0') //遊戲結束 ANS_NUM A 0 B 預設是4A0B
            {
                long second = core.getTimePassed(); //取得經過時間
                record_list.add("用時: " + second / 60 + "分" + second % 60 + "秒");
                record_list.add("猜測次數: " + core.getGuessTime() + "次");
                frame.add(reset); //放上重置
            }
        }
    }
}

class MessageBox extends Dialog
{
    private final Label message;

    public MessageBox(Frame parent, String title)
    {
        super(parent, title, true);

        this.setSize(280, 200);
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent windowEvent)
            {
                MessageBox.this.dispose();
            }
        });
        this.setLayout(null);

        final Button ok = new Button("確定");
        ok.setFont(new Font(Game.FONT_NAME, Font.PLAIN, 16));
        ok.setBounds(75, 150, 75, 25);
        ok.addActionListener((ActionListener) -> this.dispose());
        this.add(ok);

        message = new Label();
        message.setFont( new Font(Game.FONT_NAME, Font.PLAIN, 16));
        message.setBounds(25, 75, 200, 50);
        this.add(message);
    }

    public void display(String error_message)
    {
        message.setText(error_message);
        this.setVisible(true);
    }
}