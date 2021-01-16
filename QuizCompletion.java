package engine;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.IOException;

@Entity
@JsonSerialize(using = QuizCompletionSerializer.class)
        public class QuizCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(targetEntity = QuizItem.class)
    @JoinColumn(name = "quizItemId")
//    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private QuizItem quizItem;

    private String timestamp;

    @ManyToOne(targetEntity = User.class)
//    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "userId")
    private User userCompleted;

    public QuizCompletion(QuizItem q, String timestamp, User user) {
        this.quizItem = q;
        this.timestamp = timestamp;
        this.userCompleted = user;
    }

    public QuizCompletion(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public QuizItem getQuizItem() {
        return quizItem;
    }

    public void setQuizItem(QuizItem quizItem) {
        this.quizItem = quizItem;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public User getUserCompleted() {
        return userCompleted;
    }

    public void setUserCompleted(User userCompleted) {
        this.userCompleted = userCompleted;
    }
}

class QuizCompletionSerializer extends JsonSerializer<QuizCompletion> {
    @Override
    public void serialize(QuizCompletion quizCompletion, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", quizCompletion.getQuizItem().getQuizItemId());
            jsonGenerator.writeStringField("completedAt", quizCompletion.getTimestamp());
        jsonGenerator.writeEndObject();
    }
}