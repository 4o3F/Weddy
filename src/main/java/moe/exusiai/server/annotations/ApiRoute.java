package moe.exusiai.server.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiRoute {
    HttpMethod HttpMethod() default HttpMethod.GET;

    String Path();

    public enum HttpMethod {
        GET, POST, PUT, DELETE;
    }
}
