package cn.ubuilding.rpc.support.loadbalance;

import cn.ubuilding.rpc.support.annotation.LB;

import java.util.List;
import java.util.Random;

/**
 * @author Wu Jianfeng
 * @since 2016/10/23 15:02
 */
@LB(LoadBalanceFactory.RANDOM)
public class RandomLoadBalance extends AbstractLoadBalance {

    private final Random random = new Random();

    @Override
    protected String doSelect(List<String> addresses) {
        int length = addresses.size(); // 总个数
        int totalWeight = 0; // 总权重
        boolean sameWeight = true; // 权重是否都一样
        for (int i = 0; i < length; i++) {
            int weight = getWeight(addresses.get(i));
            totalWeight += weight; // 累计总权重
            if (sameWeight && i > 0 && weight != getWeight(addresses.get(i - 1))) {
                sameWeight = false; // 计算所有权重是否一样
            }
        }
        if (totalWeight > 0 && !sameWeight) {
            // 如果权重不相同且权重大于0则按总权重数随机
            int offset = random.nextInt(totalWeight);
            // 并确定随机值落在哪个片断上
            for (String address : addresses) {
                offset -= getWeight(address);
                if (offset < 0) {
                    return address;
                }
            }
        }
        // 如果权重相同或权重为0则均等随机
        return addresses.get(random.nextInt(length));
    }
}
