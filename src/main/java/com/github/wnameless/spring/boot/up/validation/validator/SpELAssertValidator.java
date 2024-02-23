package com.github.wnameless.spring.boot.up.validation.validator;

import static com.github.wnameless.spring.boot.up.validation.validator.ReflectionUtils.extractStaticMethods;
import static org.springframework.util.StringUtils.hasText;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import com.github.wnameless.spring.boot.up.validation.validator.support.RelaxedBooleanTypeConverterDecorator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SpELAssertValidator
    implements ConstraintValidator<SpELAssert, Object>, BeanFactoryAware {

  private static final Logger LOG = LoggerFactory.getLogger(SpELAssertValidator.class);
  private static final TypeConverter TYPE_CONVERTER =
      new RelaxedBooleanTypeConverterDecorator(new StandardTypeConverter());

  private Expression expression;
  private Expression applyIfExpression;
  private List<Method> functions = new LinkedList<>();
  private BeanFactory beanFactory;

  public void initialize(SpELAssert constraint) {
    ExpressionParser parser = new SpelExpressionParser();

    expression = parser.parseExpression(constraint.value());
    if (hasText(constraint.applyIf())) {
      applyIfExpression = parser.parseExpression(constraint.applyIf());
    }
    for (Class<?> clazz : constraint.helpers()) {
      functions = extractStaticMethods(clazz);
    }
  }

  public boolean isValid(Object object, ConstraintValidatorContext context) {
    if (object == null) return true;

    EvaluationContext evalContext = createEvaluationContext(object);

    if (isApplyIfValid(evalContext)) {
      LOG.trace("Evaluating expression {{}} on object: {}", expression.getExpressionString(),
          object);
      return evaluate(expression, evalContext);
    }
    return true;
  }

  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  private boolean isApplyIfValid(EvaluationContext context) {
    if (applyIfExpression == null) return true;

    LOG.trace("Evaluating applyIf {{}} on object: {}", applyIfExpression.getExpressionString(),
        context);
    return evaluate(applyIfExpression, context);
  }

  private boolean evaluate(Expression expression, EvaluationContext context) {
    Boolean result = expression.getValue(context, Boolean.class);
    return result == null ? false : result;
  }

  private StandardEvaluationContext createEvaluationContext(Object rootObject) {
    StandardEvaluationContext context = new StandardEvaluationContext();

    context.setRootObject(rootObject);
    context.setTypeConverter(TYPE_CONVERTER);

    if (beanFactory != null) {
      context.setBeanResolver(new BeanFactoryResolver(beanFactory));
    }
    if (!functions.isEmpty()) {
      for (Method helper : functions) {
        context.registerFunction(helper.getName(), helper);
      }
      LOG.trace(inspectFunctions(context));
    }

    return context;
  }

  private String inspectFunctions(EvaluationContext context) {
    StringBuilder message = new StringBuilder();
    Set<String> names = new HashSet<>(functions.size());

    message.append("Registered functions: \n");

    for (Method function : functions) {
      names.add(function.getName());
    }
    for (String name : names) {
      Object obj = context.lookupVariable(name);
      if (obj instanceof Method) {
        message.append("     #").append(name).append(" -> ").append(obj).append('\n');
      }
    }
    return message.toString();
  }

}
