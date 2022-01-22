package one_a_two_b;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

public class Core
{
    private final int[] answer;
    private final int[] avoid_repeat_array;
    private final int[] compare_answer;
    private final String regular_number;

    private Instant begin;
    private int guess_time;
    public static final byte ANS_NUM = 4; //題目的數字量 不可超過10個

    public Core()
    {
        answer = new int[ANS_NUM];
        avoid_repeat_array = new int[10];
        compare_answer = new int[10];
        regular_number = "\\d".repeat(ANS_NUM);
        reset(); //重置
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


    public CheckMessage checkAnswer(String enter_answer_str) //檢查答案
    {
        if (!enter_answer_str.matches(regular_number)) //輸入的字串如果不是ANS_NUM個數字
            return new CheckMessage(true, "請輸入 " + ANS_NUM + " 個0 ~ 9的數字");

        int[] enter_answer_int = new int[ANS_NUM]; //準備陣列 等一下要把字串轉換成陣列
        for (int i = 0; i < ANS_NUM; i++)
            enter_answer_int[i] = Character.getNumericValue(enter_answer_str.charAt(i)); //將char轉換成int 並將數字放進陣列

        resetArray(avoid_repeat_array); //重置比較陣列為0 ~ 9
        for (int i = 0; i < ANS_NUM; i++) //檢查輸入答案是否重複
        {
            if (avoid_repeat_array[enter_answer_int[i]] != -1)
                avoid_repeat_array[enter_answer_int[i]] = -1;
            else
                return new CheckMessage(true, "請勿輸入重複的數字"); //表示有數字重複
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

        guess_time++; //猜測次數 + 1
        return new CheckMessage(false, A + " A " + B + " B");
    }

    public void reset()
    {
        generateAnswer(); //產生答案並記錄到compare_answer陣列上
        begin = Instant.now(); //開始計時
        guess_time = 0;
    }

    public long getTimePassed()
    {
        Instant end = Instant.now(); //計時結束
        Duration game_time = Duration.between(begin, end);
        return game_time.toSeconds();
    }

    public int getGuessTime()
    {
        return guess_time;
    }
}

class CheckMessage
{
    private final boolean has_exception;
    private final String result;

    public CheckMessage(boolean set_code, String set_result)
    {
        has_exception = set_code;
        result = set_result;
    }

    public boolean getCode()
    {
        return has_exception;
    }

    public String getResult()
    {
        return result;
    }
}