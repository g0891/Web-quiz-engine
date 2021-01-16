package engine;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonDeserialize(using = QuizItemJsonDeserializer.class)
@JsonSerialize(using = QuizItemSerializer.class)
public class QuizItem {
    private static final Logger log = LoggerFactory.getLogger(WebQuizEngine.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "quizItemId")
    int quizItemId;

    String title;
    String text;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "userId")
    User userCreated;

    @OneToMany(
            mappedBy = "quizItem",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<QuizOption> options = new ArrayList<>();

    @OneToMany(
            mappedBy = "quizItem",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<QuizCompletion> quizCompletions = new ArrayList<>();

    QuizItem() {}

    QuizItem(String title, String text, List<QuizOption> options) {
        this.title = title;
        this.text = text;
        this.options = options;
    }

    public List<QuizOption> getOptions() {
        return options;
    }

    public void setOptions(List<QuizOption> options) {
        this.options = options;
    }

    public int getQuizItemId() {
        return quizItemId;
    }
    public void setQuizItemIdId(int id) {
        this.quizItemId = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public User getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(User userCreated) {
        this.userCreated = userCreated;
    }

}

class QuizItemJsonDeserializer extends JsonDeserializer<QuizItem> {
    @Override
    public QuizItem deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        QuizItem qi = new QuizItem();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String title = node.get("title").asText();
        String text = node.get("text").asText();

        List<QuizOption> qo = new ArrayList<>();
        JsonNode optNode = node.get("options");
        if (optNode == null) {
            qi.setOptions(null);
            return qi;
        }
        for (JsonNode option: optNode) {
            qo.add(new QuizOption(option.asText(), false, qi));
        }

        if (node.has("answer")) {
            JsonNode answerNode = node.get("answer");
            if (answerNode.isArray()) {
                for (JsonNode answer : answerNode) {
                    qo.get(answer.asInt()).setCorrect(true);
                }
            }
        }

        qi.setTitle(title);
        qi.setText(text);
        qi.setOptions(qo);
        return qi;
    }
}

class QuizItemSerializer extends JsonSerializer<QuizItem> {
    @Override
    public void serialize(QuizItem quizItem, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", quizItem.getQuizItemId());
            jsonGenerator.writeStringField("title", quizItem.getTitle());
            jsonGenerator.writeStringField("text", quizItem.getText());
            jsonGenerator.writeFieldName("options");
                jsonGenerator.writeStartArray();
                    for (QuizOption qo: quizItem.getOptions()) {
                        jsonGenerator.writeString(qo.getText());
                    }
                jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}

