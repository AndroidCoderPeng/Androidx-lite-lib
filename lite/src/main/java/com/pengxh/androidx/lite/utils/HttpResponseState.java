package com.pengxh.androidx.lite.utils;

/**
 * 网络请求状态类（用于关注返回值的情况）
 */
@SuppressWarnings("all")
public class HttpResponseState<T> {

    protected HttpResponseState() {
    }

    /**
     * 加载中状态
     */
    private static final class Loading<T> extends HttpResponseState<T> {
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

    public boolean isLoading() {
        return this instanceof Loading;
    }

    public boolean isSuccess() {
        return this instanceof Success;
    }

    public boolean isError() {
        return this instanceof Error;
    }

    public static <T> HttpResponseState<T> loading() {
        return new Loading<>();
    }

    public static <T> HttpResponseState<T> success(T body) {
        return new Success<>(body);
    }

    public static <T> HttpResponseState<T> error(Integer code, String message, Throwable ex) {
        return new Error<>(code, message, ex);
    }

    public static <T> HttpResponseState<T> error(String message) {
        return new Error<>(null, message, null);
    }
}
