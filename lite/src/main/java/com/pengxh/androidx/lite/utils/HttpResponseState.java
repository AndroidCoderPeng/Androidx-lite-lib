package com.pengxh.androidx.lite.utils;

/**
 * 网络请求状态类（用于关注返回值的情况）
 */
public class HttpResponseState<T> {
    // 私有构造函数，防止外部实例化
    private HttpResponseState() {
    }

    /**
     * 加载中状态
     */
    public static final class Loading<T> extends HttpResponseState<T> {
    }

    /**
     * 成功状态，携带数据
     */
    public static final class Success<T> extends HttpResponseState<T> {
        private final T body;

        public Success(T body) {
            this.body = body;
        }

        public T getBody() {
            return body;
        }
    }

    /**
     * 失败状态，携带错误信息
     */
    public static final class Error<T> extends HttpResponseState<T> {
        private final Integer code;
        private final String message;
        private final Throwable ex;

        public Error(Integer code, String message, Throwable ex) {
            this.code = code;
            this.message = message;
            this.ex = ex;
        }

        public Integer getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public Throwable getEx() {
            return ex;
        }
    }
}
