package com.smartclassroom.model;

public class QuizQuestion {
    private int    questionId;
    private int    quizId;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAns;
    private int    marks;

    public QuizQuestion() {}

    public int    getQuestionId() { return questionId; }
    public int    getQuizId()     { return quizId;     }
    public String getQuestion()   { return question;   }
    public String getOptionA()    { return optionA;    }
    public String getOptionB()    { return optionB;    }
    public String getOptionC()    { return optionC;    }
    public String getOptionD()    { return optionD;    }
    public String getCorrectAns() { return correctAns; }
    public int    getMarks()      { return marks;      }

    public void setQuestionId(int v)    { questionId = v; }
    public void setQuizId(int v)        { quizId     = v; }
    public void setQuestion(String v)   { question   = v; }
    public void setOptionA(String v)    { optionA    = v; }
    public void setOptionB(String v)    { optionB    = v; }
    public void setOptionC(String v)    { optionC    = v; }
    public void setOptionD(String v)    { optionD    = v; }
    public void setCorrectAns(String v) { correctAns = v; }
    public void setMarks(int v)         { marks      = v; }

    public void display(int number) {
        System.out.println("  Q" + number + ". " + question + "  [" + marks + " mark(s)]");
        System.out.println("     A. " + optionA);
        System.out.println("     B. " + optionB);
        System.out.println("     C. " + optionC);
        System.out.println("     D. " + optionD);
    }
}
