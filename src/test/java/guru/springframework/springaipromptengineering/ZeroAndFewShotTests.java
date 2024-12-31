package guru.springframework.springaipromptengineering;

import org.junit.jupiter.api.Test;
import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.UUID;

/**
 * Created by jt, Spring Framework Guru.
 */
@SpringBootTest
public class ZeroAndFewShotTests extends BaseTestClass {

    @Autowired
    OpenAiChatProperties openAiChatProperties;

    String review = """
            I get it. Everyone is buying these now after years of not caring about Stanley tumblers because of social media. The problem with viral crap like this is we get caught up in fitting in and jumping on the band wagon that we fail to see what's wrong with a product before buying it.
            THIS TUMBLER IS NOT LEAK PROOF. It's not even a little resistent to leaking. Even if you have the top fully closed and the straw taken out, the liquid inside will leak out like crazy if you tip it over even slightly. To me, if I'm going to carry around 30-40oz of hot or cold liquids then the tumbler MUST prevent said liquids from coming out. I understand it's not a water bottle, but that's a lame technicality that Stanley shouldn't cling to. At a MINIMUM the tumbler should be leak proof if I take out the straw and close the top. Furthermore, the sip top closing mechanism seems very flimsy and can be easily bended out of place, so beware of turning it too hard or especially dropping your tumbler.
            I am highly disappointed for being sucked into thinking this was a reliable tumbler that would replace others I have. Granted they are not as nice looking, but they do the job of holding AND containing the water I take with me all day to and from work in NYC.
            I do NOT recommend this tumbler and I would suggest that Stanley fix these important issues instead of focusing on more colors and patterns.""";

    String prompt = """
            Identify a list of emotions that the writer of the following reviews is expressing, and provide a brief summary of each review.
            
            Review: ```{review}```
            """;

    /**
     * Zero shot - send the model a single prompt with no hints or examples. Leverages the model's training to generate a response.
     */
    @Test
    void zeroShotPromptTest() {
        // java for loop 3 times
        for (int i = 0; i < 3; i++) {
            // java UUID randomUUID is an API cache buster
            PromptTemplate promptTemplate = new PromptTemplate(prompt,
                    Map.of("review", UUID.randomUUID() + "\n" + review));

            ChatResponse response = chatModel.call(promptTemplate.create());

            System.out.println("#################################\n");
            System.out.println(response.getResult().getOutput().getContent());
        }
    }

    @Test
    void zeroShotPromptTestWithModelOptions() {

        OpenAiChatOptions openAiChatOptions = new OpenAiChatOptions.Builder(openAiChatProperties.getOptions())
                .withTemperature(0.1) //default is 0.7
                .withModel("gpt-4-turbo-preview")
                .build();

        // java for loop 3 times
        for (int i = 0; i < 3; i++) {
            // java UUID randomUUID is an API cache buster
            PromptTemplate promptTemplate = new PromptTemplate(prompt,
                    Map.of("review" , UUID.randomUUID() + "\n" + review));

            Prompt prompt = new Prompt(promptTemplate.createMessage(), openAiChatOptions);

            ChatResponse response = chatModel.call(prompt);

            System.out.println("#################################\n");
            System.out.println(response.getResult().getOutput().getContent());
        }
    }

    /**
     * Few shot - send the model a few examples to help it understand the context of the prompt.
     *
     * Example from 'Language Models are Few-Shot Learners' paper: https://arxiv.org/abs/2005.14165
     */
    String whatpuPrompt = """
            A "whatpu" is a small, furry animal native to Tanzania. An example of a sentence that uses
            the word whatpu is:
           \s
            We were traveling in Africa and we saw these very cute whatpus.
            \s
            To do a "farduddle" means to jump up and down really fast. An example of a sentence that uses
                  the word farduddle is:
      \s""";

    @Test
    void testwhatPuPromptFewShotTest() {
        PromptTemplate promptTemplate = new PromptTemplate(whatpuPrompt);

        System.out.println(chatModel.call(promptTemplate.create()).getResult().getOutput().getContent());
    }

    String vacationPrompt = """
            John likes white sand beaches and warm weather.
            
            What are 5 locations John should consider for vacation?
            """;

    @Test
    void testVacationFewShotTest() {
        PromptTemplate promptTemplate = new PromptTemplate(vacationPrompt);

        System.out.println(chatModel.call(promptTemplate.create()).getResult().getOutput().getContent());
    }

    String mathPrompt = """
            2+2 = twotwo
            3+3 = threethree
            4+5 = fourfive
            
            What is 5+7?
            """;

    @Test
    void testMathPromptFewShotTest() {
        PromptTemplate promptTemplate = new PromptTemplate(mathPrompt);

        System.out.println(chatModel.call(promptTemplate.create()).getResult().getOutput().getContent());
    }

    @Test
    void AiHallucinationTest() {
        Prompt prompt = new Prompt("Write sales copy for the new 'professional grade' " +
                "Denali Advanced Toothbrush by GMC.");

        System.out.println(chatModel.call(prompt).getResult().getOutput().getContent());
    }
}
