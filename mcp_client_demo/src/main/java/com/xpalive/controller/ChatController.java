package com.xpalive.controller;

import com.xpalive.constants.ModelName;
import com.xpalive.dto.UserMsg;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;

@RestController
public class ChatController {

    private final OllamaChatModel chatModel;
    private final Scheduler ollamaScheduler;

    @Autowired
    public ChatController(OllamaChatModel chatModel, Scheduler ollamaScheduler) {
        this.chatModel = chatModel;
        this.ollamaScheduler = ollamaScheduler;
    }

    @PostMapping("/ai/generate")
    public Mono<ChatResponse> generate(@RequestBody UserMsg userMsg) {
        String systemPrompt = """
                你是文本精简助手。
                任务：对输入的【公司主营业务】文本归纳压缩。
                规则：
                1. 把相近的业务合并，删除营业执照模板废话、重复描述；
                2. 强制要求：通读全部文本，识别全部业务板块，**所有业务大类都要保留在结果里**，不能丢掉后半段内容，不允许只截取开头一小部分；精简是压缩句式，不是砍掉业务；
                3. 字数要求：原文 ≤ 180字，原样输出；原文>180字，控制在120～180字；
                4. 严禁凭空新增业务，内容必须全部来自原文；行文通顺；
                5. 如果快要超字数，优先压缩修饰词，而不是删掉一整个业务品类；
                6. 只返回精简后的文本，不要任何额外解释、开场白多余话。
                """;
        List<Message> messages = List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(userMsg.message())
        );
        Prompt prompt = new Prompt(messages, OllamaChatOptions.builder()
                .model(ModelName.QWEN3_17B.getName())
                .temperature(0.1)
                .enableThinking()
                .build());
        return Mono.fromCallable(() -> chatModel.call(prompt))
                .subscribeOn(ollamaScheduler);
    }

    @PostMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestBody UserMsg userMsg) {
        Prompt prompt = new Prompt(userMsg.message(), OllamaChatOptions.builder()
                .model(ModelName.QWEN3_17B.getName())
                .temperature(0.4)
                .enableThinking()
                .build());
        return this.chatModel.stream(prompt);
    }

}