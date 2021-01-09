package com.atguigu.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 校验注解
 *
 * @description
 * 可以参考 NotBlank 注解
 */

@Documented
@Constraint(validatedBy = {ListValueConstraintValidator.class})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface ListValue {

  // String message() default "{javax.validation.constraints.NotBlank.message}";
  // 这里使用自定义的message,在resource下创建新的配置文件
  String message() default "{com.atguigu.common.valid.ListValue.message}";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };

  int[] vals() default {};

}
