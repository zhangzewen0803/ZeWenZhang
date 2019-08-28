package com.tahoecn.bo.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
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

//@Configuration
public class MdmRabbitMQConfig {
	

    @Value("${mdm.rabbitmq.address}")
    private String address;//ip地址

    @Value("${mdm.rabbitmq.username}")
    private String username;//账号

    @Value("${mdm.rabbitmq.password}")
    private String password;//密码

    @Value("${mdm.rabbitmq.virtualHost}")
    private String virtualHost;//虚拟主机

    @Value("${mdm.rabbitmq.callcenterExchange}")
    private String callcenterExchange;//呼叫中心交换机

    @Value("${mdm.rabbitmq.callcenterRoutingKey}")
    private String callcenterRoutingKey;//呼叫中心路由键

    @Value("${mdm.rabbitmq.maxConcurrentConsumers}")
    private int maxConcurrentConsumers;//最大同时消费者

    @Value("${mdm.rabbitmq.concurrentConsumers}")
    private int concurrentConsumers;//同时消费者
    
    /**
     * 配置链接信息 连接工厂
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
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }
    
    /**
    *
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     *
     *
     * FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     * HeadersExchange ：通过添加属性key-value匹配
     * DirectExchange:按照routingkey分发到指定队列
     * TopicExchange:多关键字匹配
     */
    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(callcenterExchange, true, false);
    }
    
    /**
     * 配置消息队列
     * 针对消费者配置  
     * @return
     */
    @Bean  
    public Queue queue() {  
    	//这里是队列名称
       return new Queue(callcenterRoutingKey, true); 
    	
    }
    
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(defaultExchange()).with(callcenterRoutingKey);
    }
    
    /**
     * 监听异步收发消息
     * @param rabbitMqFonoutListener
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer messageContainer(MdmRabbitMqMessageListener rabbitMqFonoutListener) {
         SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
         container.setQueues(queue());
         container.setExposeListenerChannel(true);
         container.setMaxConcurrentConsumers(maxConcurrentConsumers);
         container.setConcurrentConsumers(concurrentConsumers);
         container.setAcknowledgeMode(AcknowledgeMode.MANUAL); //设置确认模式手工确认
         container.setMessageListener(rabbitMqFonoutListener);
         return container;
     } 
}
