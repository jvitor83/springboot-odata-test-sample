package com.hackerrank.projects.controllers;

import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmLiteral;
import org.apache.olingo.odata2.api.edm.EdmLiteralKind;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.EdmType;
import org.apache.olingo.odata2.api.edm.EdmTypeKind;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.uri.expression.BinaryExpression;
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator;
import org.apache.olingo.odata2.api.uri.expression.ExceptionVisitExpression;
import org.apache.olingo.odata2.api.uri.expression.ExpressionVisitor;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.api.uri.expression.LiteralExpression;
import org.apache.olingo.odata2.api.uri.expression.MemberExpression;
import org.apache.olingo.odata2.api.uri.expression.MethodExpression;
import org.apache.olingo.odata2.api.uri.expression.MethodOperator;
import org.apache.olingo.odata2.api.uri.expression.OrderByExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderExpression;
import org.apache.olingo.odata2.api.uri.expression.PropertyExpression;
import org.apache.olingo.odata2.api.uri.expression.SortOrder;
import org.apache.olingo.odata2.api.uri.expression.UnaryExpression;
import org.apache.olingo.odata2.api.uri.expression.UnaryOperator;
import org.apache.olingo.odata2.api.uri.expression.Visitable;
import org.hibernate.query.criteria.internal.predicate.BooleanExpressionPredicate;
import org.springframework.core.GenericTypeResolver;

import com.hackerrank.projects.repositories.EntityManagerHolder;
import com.hackerrank.projects.repositories.GenericSpecification;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.PredicateOperation;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanOperation;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;

public class QueryBuilder<T> implements org.apache.olingo.odata2.api.uri.expression.ExpressionVisitor {

	
	
	private JPAQuery<?> query;
	public QueryBuilder(JPAQuery<?> query) {
		this.query = query;
	}


	@Override
	public Predicate visitFilterExpression(FilterExpression filterExpression, String expressionString, Object expression) {
		
		
		return (Predicate) expression;
	}

	@Override
	public Object visitBinary(BinaryExpression binaryExpression, BinaryOperator operator, Object left,
			Object right) {
		Object retorno = null;
		 if (operator == BinaryOperator.ADD
	        || operator == BinaryOperator.MODULO
	        || operator == BinaryOperator.MUL
	        || operator == BinaryOperator.DIV
	        || operator == BinaryOperator.SUB) {
	      retorno = null; //evaluateArithmeticOperation(operator, left, right);
	    } else if (operator == BinaryOperator.EQ
	        || operator == BinaryOperator.NE
	        || operator == BinaryOperator.GE
	        || operator == BinaryOperator.GT
	        || operator == BinaryOperator.LE
	        || operator == BinaryOperator.LT) {
	      retorno = evaluateComparisonOperation(operator, (Expression<T>) left, (Expression<T>) right);
	    } else if (operator == BinaryOperator.AND
	        || operator == BinaryOperator.OR) {
	    	retorno = evaluateBooleanOperation(operator, (Expression<T>) left, (Expression<T>) right);
		  }
		 
		 return retorno;
	}
	
	@SuppressWarnings("unchecked")
	private Object evaluateBooleanOperation(BinaryOperator operator, Expression<?> left, Expression<?> right)
     {
    if(operator == BinaryOperator.AND) {    	
    	// Class<T> genericTypeClass = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), QueryBuilder.class);
    	return Expressions.predicate(Ops.AND, left, right);
    	//return this.criteriaBuilder.and((Expression<Boolean>)left, (Expression<Boolean>)right);
    } else {
    	return Expressions.predicate(Ops.OR, left, right);
    	//return this.criteriaBuilder.or((Expression<Boolean>)left, (Expression<Boolean>)right);
    }
    
}

	

	  private Object evaluateComparisonOperation(BinaryOperator operator, Expression<?> left, Expression<?> right) 
	       {
	      if (operator == BinaryOperator.EQ) {
	    	  return Expressions.predicate(Ops.EQ, left, right);
	      } else if (operator == BinaryOperator.NE) {
	    	  return Expressions.predicate(Ops.NE, left, right);
	      } else if (operator == BinaryOperator.GE) {
	    	  return Expressions.predicate(Ops.GOE, left, right);
	      } else if (operator == BinaryOperator.GT) {
	    	  return Expressions.predicate(Ops.GT, left, right);
	      } else if (operator == BinaryOperator.LE) {
	    	  return Expressions.predicate(Ops.LOE, left, right);
	      } else if (operator == BinaryOperator.LT) {
	    	  return Expressions.predicate(Ops.LT, left, right);
	      }
	      return null;
	  }


	@Override
	public Object visitOrderByExpression(OrderByExpression orderByExpression, String expressionString,
			List<Object> orders) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitOrder(OrderExpression orderExpression, Object filterResult, SortOrder sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression<?> visitLiteral(LiteralExpression literal, EdmLiteral edmLiteral) {
		//Expression<String> expression = this.criteriaBuilder.literal(literal.getUriLiteral());
		//return expression;A
		String typeName = null;
		try {
			typeName = literal.getEdmType().getName();
		} catch (EdmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(typeName == EdmSimpleTypeKind.String.getFullQualifiedName().getName())
		{
			// org.apache.olingo.odata2.api.edm.
			return Expressions.constant(literal.getUriLiteral());
		}
		
		EdmType type = literal.getEdmType();
		EdmTypeKind typeKind = type.getKind();
		
		// Boolean a = type == org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind.String;
		try {
			String name = type.getName();
			String stringName = org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind.String.getFullQualifiedName().getName();
			Boolean b = name == stringName;
		} catch (EdmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return Expressions.constant(literal.getUriLiteral());
	}

	@Override
	public Object visitMethod(MethodExpression methodExpression, MethodOperator method, List<Object> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMember(MemberExpression memberExpression, Object path, Object property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitProperty(PropertyExpression propertyExpression, String uriLiteral, EdmTyped edmProperty) {
		// TODO Auto-generated method stub
		/*EntityType<T> modelType = root.getModel();
		Class<T> classType = modelType.getBindableJavaType();
		ParameterExpression<T> parameterExpression = this.criteriaBuilder.parameter(classType, uriLiteral);
		return parameterExpression;
		*/
		Class<T> classType = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), QueryBuilder.class);
		return Expressions.path(classType, uriLiteral);
	}

	@Override
	public Object visitUnary(UnaryExpression unaryExpression, UnaryOperator operator, Object operand) {
		// TODO Auto-generated method stub
		return null;
	}


}
