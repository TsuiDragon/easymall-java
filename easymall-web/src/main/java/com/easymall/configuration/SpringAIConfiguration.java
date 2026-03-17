package com.easymall.configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SpringAIConfiguration {

    @Value("${spring.ai.dashscope.base-url:}")
    private String dashscopeBaseUrl;

    @Value("${spring.ai.dashscope.api-key:}")
    private String dashscopeApiKey;

    @Value("${spring.ai.dashscope.embedding.model:}")
    private String dashscopeEmbeddingModel;

    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel) {
        Map<String, Object> extraBody = new HashMap<>();
        extraBody.put("enable_thinking", false);
        return ChatClient.builder(openAiChatModel).defaultOptions(OpenAiChatOptions.builder().extraBody(extraBody).build()).build();
    }

    @Primary
    @Bean
    public EmbeddingModel embeddingModel(OpenAiEmbeddingModel openAiEmbeddingModel) {
        return openAiEmbeddingModel;
    }


    //如果 使用 deepseek的chat 模型，使用百炼的向量模型，那么启用下面的这个，同时配置 百炼的相关配置
/*    @Bean
    @Primary
    public EmbeddingModel embeddingModel() {
        var openAiApi = OpenAiApi.builder()
                .baseUrl(dashscopeBaseUrl)
                .apiKey(dashscopeApiKey)
                .build();
        return new OpenAiEmbeddingModel(
                openAiApi,
                MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder()
                        .model(dashscopeEmbeddingModel)
                        .build(),
                RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }*/


    //本地ollama 配置
/*
    @Bean
    @Primary
    public ChatClient chatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel)
                .defaultOptions(OllamaChatOptions.builder().disableThinking().build())//禁用思考
                .build();
    }
*/

/*    @Bean
    @Primary
    public EmbeddingModel embeddingModel(OllamaEmbeddingModel ollamaEmbeddingModel) {
        return ollamaEmbeddingModel;
    }*/
}

