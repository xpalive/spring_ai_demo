package com.xpalive.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OllamaConfig {
    @Bean
    public OllamaChatModel ollamaChatModel(OllamaApi ollamaApi) {
        // 这里是全局默认参数；代码调用时可以传 Prompt + 自定义 OllamaOptions 覆盖
        OllamaChatOptions defaultOptions = OllamaChatOptions.builder()
                .model("qwen3:0.6b") // 换成你自己的模型名
                .temperature(0.3)  // - 靠近 0：回答更确定、严谨、少脑洞、适合问答、事实类（比如阅读理解、数学） - 靠近 1：更有创造力、自由发挥
                .build();

        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .options(defaultOptions)
                .build();
    }

    @Bean
    public ChatClient chatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel)
                // 可以在这里设置全局默认prompt、全局工具、全局Advisor
                // .defaultSystemPrompt("你是语文老师，回答简洁，适合六年级学生")
                // .defaultTools()
                .build();
    }
}
