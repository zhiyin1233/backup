package com.yiziton.dataweb.core.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基本工具类
 *
 * @author lujijiang
 */
public class Lang {

    private static Logger logger = LoggerFactory.getLogger(Lang.class);

    /**
     * 将CheckedException转换为RuntimeException.
     */
    public static RuntimeException unchecked(Throwable e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new RuntimeException(e);
        }
    }

    /**
     * 将CheckedException转换为RuntimeException.
     */
    public static RuntimeException unchecked(Throwable e, String message, Object... args) {
        return new RuntimeException(String.format(message, args), e);
    }

    /**
     * 快速生成Map
     *
     * @param args 参数列表，奇数位参数是健，偶数位参数是值，必须是偶数个参数
     * @param <K>
     * @param <V>
     * @return 由参数生成的map
     */
    public static <K, V> Map<K, V> map(Object... args) {
        if (args == null) {
            return null;
        }
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("The number of parameters must be even");
        }
        Map<K, V> map = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            K key = (K) args[i];
            V value = (V) args[i + 1];
            map.put(key, value);
        }
        return map;
    }

    /**
     * 打印消息，统一使用此处打印
     *
     * @param arg
     * @param args
     */
    public static void printOut(Object arg, Object... args) {
        String mark = System.getProperty("lang.println");
        mark = mark == null ? "default" : mark;
        switch (mark) {
            case "none":return;
            case "trace":
                if (logger.isTraceEnabled()) {
                    logger.trace(fillMessage(arg, args));
                }
                break;
            case "debug":
                if (logger.isDebugEnabled()) {
                    logger.debug(fillMessage(arg, args));
                }
                break;
            case "info":
                if (logger.isInfoEnabled()) {
                    logger.info(fillMessage(arg, args));
                }
                break;
            case "warn":
                if (logger.isWarnEnabled()) {
                    logger.warn(fillMessage(arg, args));
                }
                break;
            case "error":
                if (logger.isErrorEnabled()) {
                    logger.error(fillMessage(arg, args));
                }
                break;
            default:
                System.out.println(new Date().toString() + fillMessage(arg, args));
        }
    }

    /**
     * 打印消息，统一使用此处打印
     *
     * @param arg
     * @param args
     */
    public static void printErr(Object arg, Object... args) {
        String mark = System.getProperty("lang.println");
        mark = mark == null ? "default" : mark;
        switch (mark) {
            case "none":return;
            case "trace":
                if (logger.isTraceEnabled()) {
                    logger.trace(fillMessage(arg, args));
                }
                break;
            case "debug":
                if (logger.isDebugEnabled()) {
                    logger.debug(fillMessage(arg, args));
                }
                break;
            case "info":
                if (logger.isInfoEnabled()) {
                    logger.info(fillMessage(arg, args));
                }
                break;
            case "warn":
                if (logger.isWarnEnabled()) {
                    logger.warn(fillMessage(arg, args));
                }
                break;
            case "error":
                if (logger.isErrorEnabled()) {
                    logger.error(fillMessage(arg, args));
                }
                break;
            default:
                System.err.println(new Date().toString() + fillMessage(arg, args));
        }
    }

    private static String fillMessage(Object arg, Object[] args) {
        args = args ==null ? new Object[]{null}:args;
        String message = String.format(String.valueOf(arg), args);
        // 获取执行类和行号
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[2];
        String className = stackTraceElement.getClassName();
        int line = stackTraceElement.getLineNumber();
        message = className + ":" + line + " " + message;
        return message;
    }

    public static Throwable getCause(Throwable e) {
        while (e.getCause()!=null){
            e = e.getCause();
        }
        return e;
    }
}
