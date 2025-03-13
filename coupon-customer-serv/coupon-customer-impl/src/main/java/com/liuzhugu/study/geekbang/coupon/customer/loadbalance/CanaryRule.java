package com.liuzhugu.study.geekbang.coupon.customer.loadbalance;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.ServiceInstanceListSuppliers;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.liuzhugu.study.geekbang.coupon.customer.constant.Constant.TRAFFIC_VERSION;
/**
 * 金丝雀负载均衡策略
 * */
@Slf4j
public class CanaryRule implements ReactorServiceInstanceLoadBalancer {

    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private String serviceId;

    //定义一个轮询策略的种子
    final AtomicInteger position;

    public CanaryRule(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                      String serviceId){
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        position = new AtomicInteger(new Random().nextInt(1000));
    }

    //负载均衡策略选择器的入口
    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                //初始化
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next()
                .map(serviceInstances -> processInstanceResponse(supplier,serviceInstances,request));
    }

    private Response<ServiceInstance> processInstanceResponse(
            ServiceInstanceListSupplier supplier,
            List<ServiceInstance> serviceInstances,
            Request request) {
        //对金丝雀标记进行处理  得到响应
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances,request);

        //
        if (supplier instanceof SelectedInstanceCallback &&
                serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    //根据金丝雀规则返回目标节点
    Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances,Request request) {
        //注册中心无实例  抛出异常
        if (CollectionUtils.isEmpty(instances)) {
            log.warn("No instance available {}", serviceId);
            return new EmptyResponse();
        }

        //从WebClient请求Header中获取特定的流量打标值
        //注意 以下代码仅适用于WebClient调用  如果使用RestTemplate或者Feign则需要额外适配
        DefaultRequestContext context = (DefaultRequestContext) request.getContext();
        RequestData requestData = (RequestData) context.getClientRequest();
        HttpHeaders headers = requestData.getHeaders();
        //获取head中的流量标记
        String trafficVersion = headers.getFirst(TRAFFIC_VERSION);

        //如果没找到打标标记  或者标记为空 则使用RoundRobin规则进行轮询
        if (StringUtils.isBlank(trafficVersion)) {
            //过滤掉所有金丝雀测试的节点(即Nacos Metadaba中包含流量标记的节点)
            List<ServiceInstance> noneCanaryInstance = instances.stream()
                    .filter(e -> !e.getMetadata().containsKey(TRAFFIC_VERSION))
                    .collect(Collectors.toList());
            return getRoundRobinInstance(noneCanaryInstance);
        }

        //如果WebClient请求的Header里包含流量标记
        //循环每个Nacos服务节点   过滤出metadata值相同的instance  再轮询
        List<ServiceInstance> canaryInstance = instances.stream()
                .filter(e -> {
                    String trafficVersionInMetadata = e.getMetadata().get(TRAFFIC_VERSION);
                    return StringUtils.endsWithIgnoreCase(trafficVersionInMetadata,trafficVersion);
                })
                .collect(Collectors.toList());
        return getRoundRobinInstance(canaryInstance);
    }

    //使用轮询机制获取节点
    private Response<ServiceInstance> getRoundRobinInstance(List<ServiceInstance> instances) {
        //如果没有可用节点  则返回空
        if (instances.isEmpty()) {
            log.warn("No servers available for service: " + serviceId);
            return new EmptyResponse();
        }
        //每一次计数器都自动 + 1  实现轮询效果
        int pos = Math.abs(this.position.incrementAndGet());
        ServiceInstance instance = instances.get(pos % instances.size());

        return new DefaultResponse(instance);
    }
}
