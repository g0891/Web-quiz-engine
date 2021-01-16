package engine;

import javax.persistence.*;

@Entity
public class QuizOption {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    String text;
    Boolean correct;
    @ManyToOne(targetEntity = QuizItem.class)
    @JoinColumn(name = "quizItemId")
    QuizItem quizItem;

    QuizOption(){}

    QuizOption(String text, Boolean correct, QuizItem qi) {
        this.text = text;
        this.correct = correct;
        this.quizItem = qi;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }
}
