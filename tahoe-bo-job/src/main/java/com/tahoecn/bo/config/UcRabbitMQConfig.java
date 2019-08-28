package com.tahoecn.bo.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @ClassName：ConsumerConfig
 * @Description：TODO(这里用一句话描述这个类的作用) 
 * @date 2019年5月28日 下午3:40:57 
 * @version 1.0.0 
 */
@Configuration
public class UcRabbitMQConfig {
	
    @Value("${uc.rabbitmq.addresses}")
    private String address;

    @Value("${uc.rabbitmq.username}")
    private String username;

    @Value("${uc.rabbitmq.password}")
    private String password;

    @Value("${uc.rabbitmq.virtual-host}")
    private String virtualHost;

    @Value("${uc.rabbitmq.template.exchange}")
    private String exchange;

    @Value("${uc.rabbitmq.ucQueueName}")
    private String ucQueueName;

    @Value("${uc.rabbitmq.maxConcurrentConsumers}")
    private int maxConcurrentConsumers;

    @Value("${uc.rabbitmq.concurrentConsumers}")
    private int concurrentConsumers;
    
    /**
     * 配置链接信息
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(address);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirms(true); // 必须要设置
        return connectionFactory;
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }
    
    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     *
     * FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     * HeadersExchange ：通过添加属性key-value匹配
     * DirectExchange:按照routingkey分发到指定队列
     * TopicExchange:多关键字匹配
     */
    @Bean
    public FanoutExchange ucFanoutExchange() {
        return new FanoutExchange(exchange);
    }
    
    /**
     * 配置消息队列
     * 针对消费者配置  
     * @return
     */
    @Bean  
    public Queue ucQueue() {  
       return new Queue(ucQueueName, true); 
    }
    
    @Bean  
    public UcRabbitMQMessageListener messageListener() {  
       return new UcRabbitMQMessageListener(); 
    }
    
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(ucQueue()).to(ucFanoutExchange());
    }
    
    @Bean
    public SimpleMessageListenerContainer messageContainer() {
         SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
         container.setQueues(ucQueue());
         container.setExposeListenerChannel(true);
         container.setMaxConcurrentConsumers(maxConcurrentConsumers);
         container.setConcurrentConsumers(concurrentConsumers);
         container.setAcknowledgeMode(AcknowledgeMode.MANUAL); //设置确认模式手工确认
         container.setMessageListener(messageListener());
         return container;
     }
}
