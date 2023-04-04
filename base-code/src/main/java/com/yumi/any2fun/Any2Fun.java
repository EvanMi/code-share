package com.yumi.any2fun;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.function.ToIntFunction;

import static java.lang.invoke.MethodType.methodType;

public class Any2Fun {

    public static void main(String[] args) throws Throwable{
        /**
         * 黑科技第一步，拿到潘多拉魔盒的钥匙 IMPL_LOOKUP
         * 这个lookup没有任何校验，你懂得。
         * */
        Class<MethodHandles.Lookup> lookupClass = MethodHandles.Lookup.class;
        Field implLookup = lookupClass.getDeclaredField("IMPL_LOOKUP");
        implLookup.setAccessible(true);
        MethodHandles.Lookup lookup = (MethodHandles.Lookup)implLookup.get(null);

        /**
         * 黑科技第二步，找到自己要的东西
         * */
        MethodHandle coder = lookup
                .in(String.class)
                .findSpecial(
                        String.class,
                        "coder",
                        methodType(byte.class),
                        String.class
                );

        /**
         * 黑科技第三步，伪装
         * */

        CallSite applyAsInt = LambdaMetafactory.metafactory(
                lookup,
                "applyAsInt",
                methodType(ToIntFunction.class),
                methodType(int.class, Object.class),
                coder,
                methodType(byte.class, String.class)
        );

        ToIntFunction<String> strCoder
                = (ToIntFunction<String>) applyAsInt.getTarget().invoke();

        int yumiCoder = strCoder.applyAsInt("yumi");

        System.out.println(yumiCoder);

    }
}
