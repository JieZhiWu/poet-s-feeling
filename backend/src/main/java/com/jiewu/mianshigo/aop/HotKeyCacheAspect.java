import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import com.jiewu.mianshigo.annotation.HotKeyCache;
import com.jiewu.mianshigo.model.dto.questionBank.QuestionBankQueryRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class HotKeyCacheAspect {

    @Around("@annotation(hotKeyCache)")
    public Object around(ProceedingJoinPoint joinPoint, HotKeyCache hotKeyCache) throws Throwable {
        // 1. 获取方法参数
        Object[] args = joinPoint.getArgs();
        int idParamIndex = hotKeyCache.idParamIndex();

        // 校验参数索引合法性
        if (idParamIndex < 0 || idParamIndex >= args.length) {
            throw new IllegalArgumentException("idParamIndex 超出参数范围");
        }

        // 2. 提取ID生成缓存key
        Object idParam = args[idParamIndex];
        Long id = extractId(idParam); // 提取ID的工具方法
        String key = hotKeyCache.keyPrefix() + id;

        // 3. 热key探测与本地缓存查询
        if (JdHotKeyStore.isHotKey(key)) {
            Object cachedValue = JdHotKeyStore.get(key);
            if (cachedValue != null) {
                return cachedValue; // 缓存命中，直接返回
            }
        }

        // 4. 执行原方法获取结果
        Object result = joinPoint.proceed();

        // 5. 存入本地缓存（不传入过期时间，适配smartSet的2参数版本）
        JdHotKeyStore.smartSet(key, result);

        return result;
    }

    // 工具方法：从参数对象中提取ID（根据实际业务调整）
    private Long extractId(Object param) {
        if (param instanceof QuestionBankQueryRequest) {
            // 如果参数是QuestionBankQueryRequest类型，直接获取id
            return ((QuestionBankQueryRequest) param).getId();
        } else if (param instanceof Long) {
            // 如果参数直接是Long类型的ID
            return (Long) param;
        } else {
            throw new IllegalArgumentException("不支持的ID参数类型：" + param.getClass().getName());
        }
    }
}
    