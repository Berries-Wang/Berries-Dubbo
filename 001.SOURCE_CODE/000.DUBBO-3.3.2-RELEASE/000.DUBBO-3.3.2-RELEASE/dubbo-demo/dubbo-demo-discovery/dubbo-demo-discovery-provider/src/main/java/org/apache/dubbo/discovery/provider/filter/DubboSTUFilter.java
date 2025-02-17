package org.apache.dubbo.sentinel.provider.filter;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;

/**
 * @ClassName: DubboFilterStu
 * @Description: '学习 Dubbo Filter'
 * @Author: 'Wei.Wang'
 * @Date: 2025/2/15 20:57
 **/
@Activate(group = PROVIDER)
public class DubboSTUFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        System.out.println("------> 这是自定义Filter");
        return invoker.invoke(invocation);
    }
}
