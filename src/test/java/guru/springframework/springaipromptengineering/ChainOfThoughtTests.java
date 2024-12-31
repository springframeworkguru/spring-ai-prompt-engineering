package guru.springframework.springaipromptengineering;

import org.junit.jupiter.api.Test;
import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Created by jt, Spring Framework Guru.
 */
public class ChainOfThoughtTests extends BaseTestClass {

    @Autowired
    OpenAiChatProperties openAiChatProperties;

    /*
      Chain of thought - adding a series of intermediate reasoning steps to the prompt.
      See - https://arxiv.org/abs/2201.11903
     */
    @Test
    void testTraditionalPrompt() {
        String prompt = """
                Q: Roger has 5 tennis balls. He buys 2 more cans of tennis balls, each containing 3 balls. \s
                How many tennis balls does Roger have now?
                """;

        PromptTemplate promptTemplate = new PromptTemplate(prompt);

        ChatResponse response = chatModel.call(promptTemplate.create());

        //models previously would answer 27
        System.out.println(response.getResult().getOutput().getContent());
    }

    @Test
    void testChainOfThroughPrompt() {
        String chainOfThoughtPrompt = """
                Q: Roger has 5 tennis balls. He buys 2 more cans of tennis balls, each containing 3 balls. \s
                How many tennis balls does Roger have now?
                
                A: Roger started with 5 balls. 2 cans of 3 balls each is 6 balls. 5 + 6 = 11. So Roger has 11 tennis balls.
                
                Q: The cafeteria had 23 apples originally. They used 20 apples to make lunch and bought 6 more. How many \s
                apples does the cafeteria have now?
                """;

        PromptTemplate promptTemplate = new PromptTemplate(chainOfThoughtPrompt);

        ChatResponse response = chatModel.call(promptTemplate.create());

        System.out.println(response.getResult().getOutput().getContent());
    }

    @Test
    void testTraditionalPrompt2() {
        String prompt = """
                Q: Roger has 5 tennis balls. He buys 2 more cans of tennis balls, each containing 3 balls. \s
                How many tennis balls does Roger have now? Answer in 1 word.
                """;

        PromptTemplate promptTemplate = new PromptTemplate(prompt);

        ChatResponse response = chatModel.call(promptTemplate.create());

        //models previously would answer 27
        System.out.println(response.getResult().getOutput().getContent());
    }
}
