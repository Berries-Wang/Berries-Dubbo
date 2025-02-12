# DUbbo负载均衡策略
> 先阅读:[负载均衡策略与配置细节](./负载均衡策略与配置细节%20_%20Apache%20Dubbo.pdf) & [自适应负载均衡与限流 _ Apache Dubbo](./自适应负载均衡与限流%20_%20Apache%20Dubbo.pdf)


Dubbo 默认的负载均衡策略： Weighted Random LoadBalance（加权随机） ， 还要注意: 
1. ConsistentHash LoadBalance(一致性哈希) ， 确定的入参和确定的提供者，适用于有状态的请求(如 需要会话 )

---

## 负载均衡生效环节
![1ae6e28c70ec0be88a53006d37103f2f.png](./../005.IMGS/1ae6e28c70ec0be88a53006d37103f2f.png)