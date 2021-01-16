package engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@RestController
class QuizController {
    private static final Logger log = LoggerFactory.getLogger(QuizController.class);

    @Autowired
    QuizItemsRepository quizItems;
    @Autowired
    UserRepository userRepository;
    @Autowired
    QuizCompletionsRepository quizCompletionsRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping(path = "/api/register")
    public String register() {
        return "<h1>Hello</h1>";
    }

    @PostMapping(path = "/actuator/shutdown")
    public void shutdown(){}

    @PostMapping(path = "/api/register")
    public void register(@RequestBody User user) {
        Optional<User> userFromDb = userRepository.findByEmail(user.getEmail());
        if (userFromDb.isPresent()
                || user.getPassword() == null || user.getPassword().length() < 5
                || user.getEmail() == null || !user.getEmail().matches(".+@.+\\..+")
        ) {
            log.info("There is already such a user");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "(Bad request)");
        }
        log.info("new user");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        log.info("user saved");
    }

    @PostMapping(path = "/api/quizzes")
    public QuizItem addQuiz(@RequestBody QuizItem quizItem, Principal principal) {
        if (
                quizItem.text == null || quizItem.text.equals("")
                        || quizItem.title ==null || quizItem.title.equals("")
                        || quizItem.options == null || quizItem.options.size() < 2
        ) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "(Bad request)");
        quizItem.setUserCreated(userRepository.findByEmail(principal.getName()).get());
        quizItems.save(quizItem);
        return quizItem;
    }

    @GetMapping(path = "/api/quizzes/{id}")
    public QuizItem getQuizById(@PathVariable int id) {
        if (!quizItems.findById(id).isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "(Not found)");
        return quizItems.findById(id).get();
    }

    @GetMapping(path = "/api/quizzes/completed")
    public Page<QuizCompletion> getCompleted(@RequestParam Optional<Integer> page, Principal principal) {
        return quizCompletionsRepository.findByUserCompleted(userRepository.findByEmail(principal.getName()).get(),
                PageRequest.of(page.orElse(0), 10, Sort.Direction.DESC, "timestamp"));
    }

    @GetMapping(path = "/api/quizzes")
//    public QuizItem[] getAllQuizzes() {
//        List<QuizItem> qList = new ArrayList<>();
//        for (QuizItem quizItem: quizItems.findAll()) {
//            qList.add(quizItem);
//        }
//        return qList.toArray(new QuizItem[0]);
//    }
    public Page<QuizItem> getAllQuizzes(@RequestParam Optional<Integer> page) {
        return quizItems.findAll(PageRequest.of(page.orElse(0), 10));
    }

    @DeleteMapping(path = "/api/quizzes/{id}")
    public void deleteQuiz(@PathVariable int id, Principal principal, HttpServletResponse response) {
        if (!quizItems.findById(id).isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "(Not found)");
        if (quizItems.findById(id).get().userCreated == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "(Forbidden)");
        }
        if (userRepository.findByEmail(principal.getName()).get().getUserId() != quizItems.findById(id).get().userCreated.getUserId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "(Forbidden)");
        quizItems.deleteById(id);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);

    }



    @PostMapping(path = "/api/quizzes/{id}/solve")
    public Result checkAnswer(@PathVariable int id, @RequestBody Answer answer, Principal principal) {
        if (!quizItems.findById(id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "(Not found)");
        }
        Arrays.sort(answer.answer);
        List<Integer> correctAnswersList = new ArrayList<>();
        List<QuizOption> qoList = quizItems.findById(id).get().options;
        for (int i = 0; i < qoList.size(); i++) {
            if (qoList.get(i).getCorrect()) {
                correctAnswersList.add(i);
            }
        }
        int[] correctAnswers = new int[correctAnswersList.size()];
        for (int i = 0; i < correctAnswersList.size(); i++) {
            correctAnswers[i] = correctAnswersList.get(i);
        }
        if (Arrays.equals(answer.answer, correctAnswers)) {
            QuizCompletion qc = new QuizCompletion(quizItems.findById(id).get(), LocalDateTime.now().toString(), userRepository.findByEmail(principal.getName()).get());
            quizCompletionsRepository.save(qc);
            return new Result(true, "Congratulations, you're right!");
        } else {
            return new Result(false, "Wrong answer! Please, try again.");
        }
    }


}