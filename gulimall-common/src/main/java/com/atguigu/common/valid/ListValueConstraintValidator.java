package com.atguigu.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * 约束校验器
 *
 * @author: kaiyi
 * @create: 2020-08-21 11:30
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {

  private Set<Integer> set = new HashSet<Integer>();

  /**
   * 初始化方法
   *
   * @param constraintAnnotation
   */
  @Override
  public void initialize(ListValue constraintAnnotation) {
    int[] vals = constraintAnnotation.vals();
    for (int val : vals) {
      set.add(val);
    }

  }

  /**
   * 判断是否校验成功
   *
   * @param value 需要校验的值
   * @param context
   * @return
   */
  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    return set.contains(value);
  }
}
