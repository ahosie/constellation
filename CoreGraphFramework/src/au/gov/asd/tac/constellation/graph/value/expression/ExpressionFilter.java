/*
 * Copyright 2010-2020 Australian Signals Directorate
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.gov.asd.tac.constellation.graph.value.expression;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import au.gov.asd.tac.constellation.graph.value.expression.ExpressionParser.Expression;
import au.gov.asd.tac.constellation.graph.value.expression.ExpressionParser.Operator;
import au.gov.asd.tac.constellation.graph.value.expression.ExpressionParser.OperatorExpression;
import au.gov.asd.tac.constellation.graph.value.expression.ExpressionParser.SequenceExpression;
import au.gov.asd.tac.constellation.graph.value.expression.ExpressionParser.StringExpression;
import au.gov.asd.tac.constellation.graph.value.expression.ExpressionParser.VariableExpression;
import au.gov.asd.tac.constellation.graph.value.IndexedReadable;
import au.gov.asd.tac.constellation.graph.value.converter.ConverterRegistry;
import au.gov.asd.tac.constellation.graph.value.readables.And;
import au.gov.asd.tac.constellation.graph.value.readables.Assign;
import au.gov.asd.tac.constellation.graph.value.readables.Contains;
import au.gov.asd.tac.constellation.graph.value.readables.Difference;
import au.gov.asd.tac.constellation.graph.value.readables.EndsWith;
import au.gov.asd.tac.constellation.graph.value.readables.Equals;
import au.gov.asd.tac.constellation.graph.value.readables.ExclusiveOr;
import au.gov.asd.tac.constellation.graph.value.readables.GreaterThan;
import au.gov.asd.tac.constellation.graph.value.readables.GreaterThanOrEquals;
import au.gov.asd.tac.constellation.graph.value.readables.LessThan;
import au.gov.asd.tac.constellation.graph.value.readables.LessThanOrEquals;
import au.gov.asd.tac.constellation.graph.value.readables.Modulus;
import au.gov.asd.tac.constellation.graph.value.readables.Negative;
import au.gov.asd.tac.constellation.graph.value.readables.Not;
import au.gov.asd.tac.constellation.graph.value.readables.NotEquals;
import au.gov.asd.tac.constellation.graph.value.readables.Or;
import au.gov.asd.tac.constellation.graph.value.readables.Positive;
import au.gov.asd.tac.constellation.graph.value.readables.Product;
import au.gov.asd.tac.constellation.graph.value.readables.Quotient;
import au.gov.asd.tac.constellation.graph.value.readables.StartsWith;
import au.gov.asd.tac.constellation.graph.value.readables.Sum;
import au.gov.asd.tac.constellation.graph.value.Readable;
import au.gov.asd.tac.constellation.graph.value.types.integerType.IntValue;

/**
 *
 * @author sirius
 */
public class ExpressionFilter {
    
    private static final Map<Operator, Class> OPERATOR_CLASSES = new EnumMap<>(Operator.class);
    private static final Map<Operator, Class> CONVERTER_CLASSES = new EnumMap<>(Operator.class);
    static {
        OPERATOR_CLASSES.put(Operator.ADD, Sum.class);
        OPERATOR_CLASSES.put(Operator.SUBTRACT, Difference.class);
        OPERATOR_CLASSES.put(Operator.MULTIPLY, Product.class);
        OPERATOR_CLASSES.put(Operator.DIVIDE, Quotient.class);
        OPERATOR_CLASSES.put(Operator.MODULO, Modulus.class);
        OPERATOR_CLASSES.put(Operator.EQUALS, Equals.class);
        OPERATOR_CLASSES.put(Operator.NOT_EQUALS, NotEquals.class);
        OPERATOR_CLASSES.put(Operator.GREATER_THAN, GreaterThan.class);
        OPERATOR_CLASSES.put(Operator.GREATER_THAN_OR_EQUALS, GreaterThanOrEquals.class);
        OPERATOR_CLASSES.put(Operator.LESS_THAN, LessThan.class);
        OPERATOR_CLASSES.put(Operator.LESS_THAN_OR_EQUALS, LessThanOrEquals.class);
        OPERATOR_CLASSES.put(Operator.AND, And.class);
        OPERATOR_CLASSES.put(Operator.OR, Or.class);
        OPERATOR_CLASSES.put(Operator.EXCLUSIVE_OR, ExclusiveOr.class);
        OPERATOR_CLASSES.put(Operator.CONTAINS, Contains.class);
        OPERATOR_CLASSES.put(Operator.STARTS_WITH, StartsWith.class);
        OPERATOR_CLASSES.put(Operator.ENDS_WITH, EndsWith.class);
        
        CONVERTER_CLASSES.put(Operator.SUBTRACT, Negative.class);
        CONVERTER_CLASSES.put(Operator.ADD, Positive.class);
        CONVERTER_CLASSES.put(Operator.NOT, Not.class);
    }
    
    public static IndexedReadable<?> createExpressionIndexedReadable(SequenceExpression expression, IndexedReadableProvider indexedReadableProvider, ConverterRegistry converterRegistry) {
        final List<Expression> children = expression.getChildren();
        switch (children.size()) {
            case 1: {
                final var indexedReadable = createIndexedReadable(children.get(0), indexedReadableProvider, converterRegistry);
                if (indexedReadable == null) {
                    throw new IllegalArgumentException("Invalid expression size: " + children.size());
                }
                return indexedReadable;
            }
            
            case 2: {
                final OperatorExpression operator = (OperatorExpression)children.get(0);
                final Expression right = children.get(1);
                
                final Class converterClass = CONVERTER_CLASSES.get(operator.getOperator());
                
                final var rightIndexedReadable = createIndexedReadable(right, indexedReadableProvider, converterRegistry);
                
                if (rightIndexedReadable == null) {
                    throw new IllegalArgumentException("Unable to perform unary operation on constant");
                }
                
                return Filter.createFilter(rightIndexedReadable, converterClass, converterRegistry); 
            }
            
            case 3: {
                final Expression left = children.get(0);
                final OperatorExpression operator = (OperatorExpression)children.get(1);
                final Expression right = children.get(2);
                
                final Class operatorClass = OPERATOR_CLASSES.get(operator.getOperator());
                
                final var leftIndexedReadable = createIndexedReadable(left, indexedReadableProvider, converterRegistry);
                final var rightIndexedReadable = createIndexedReadable(right, indexedReadableProvider, converterRegistry);
                
                if (leftIndexedReadable == null) {
                    final var leftContent = ((StringExpression)left).getContent();
                    if (rightIndexedReadable == null) {
                        throw new IllegalArgumentException("Unable to perform operator on 2 constants");
                    }
                    return Filter.createFilter(leftContent, rightIndexedReadable, operatorClass, converterRegistry);
                } else if (rightIndexedReadable == null) {
                    final var rightContent = ((StringExpression)right).getContent();
                    return Filter.createFilter(leftIndexedReadable, rightContent, operatorClass, converterRegistry);
                } else {
                    return Filter.createFilter(leftIndexedReadable, rightIndexedReadable, operatorClass, converterRegistry);
                }
            }
            
            default:
                throw new IllegalArgumentException("Invalid expression size: " + children.size());
        }
    }
    
    private static IndexedReadable<?> createIndexedReadable(Expression expression, IndexedReadableProvider indexedReadableProvider, ConverterRegistry converterRegistry) {
        if (expression instanceof SequenceExpression) {
            return createExpressionIndexedReadable((SequenceExpression)expression, indexedReadableProvider, converterRegistry);
        } else if (expression instanceof VariableExpression) {
            final var variableName = ((VariableExpression)expression).getContent();
            final var indexedReadable = indexedReadableProvider.getIndexedReadable(variableName);
            if (indexedReadable == null) {
                throw new IllegalArgumentException("Unknown variable: " + variableName);
            }
            return indexedReadable;
        } else {
            return null;
        }
    }
    
    public static Readable<?> createExpressionReadable(SequenceExpression expression, IndexedReadableProvider indexedReadableProvider, IntValue indexValue, ConverterRegistry converterRegistry) {
        final List<Expression> children = expression.getChildren();
        switch (children.size()) {
            case 1: {
                final var readable = createReadable(children.get(0), indexedReadableProvider, indexValue, converterRegistry);
                if (readable == null) {
                    throw new IllegalArgumentException("Invalid expression size: " + children.size());
                }
                return readable;
            }
            
            case 2: {
                final OperatorExpression operator = (OperatorExpression)children.get(0);
                final Expression right = children.get(1);
                
                final Class converterClass = CONVERTER_CLASSES.get(operator.getOperator());
                
                final var rightReadable = createReadable(right, indexedReadableProvider, indexValue, converterRegistry);
                
                if (rightReadable == null) {
                    throw new IllegalArgumentException("Unable to perform unary operation on constant");
                }
                
                return Filter.createReadable(rightReadable, converterClass, converterRegistry); 
            }
            
            case 3: {
                final Expression left = children.get(0);
                final OperatorExpression operator = (OperatorExpression)children.get(1);
                final Expression right = children.get(2);
                
                final Class operatorClass = OPERATOR_CLASSES.get(operator.getOperator());
                
                final var leftReadable = createReadable(left, indexedReadableProvider, indexValue, converterRegistry);
                final var rightReadable = createReadable(right, indexedReadableProvider, indexValue, converterRegistry);
                
                if (leftReadable == null) {
                    final var leftContent = ((StringExpression)left).getContent();
                    if (rightReadable == null) {
                        throw new IllegalArgumentException("Unable to perform operator on 2 constants");
                    }
                    return Filter.createFilter(leftContent, rightReadable, operatorClass, converterRegistry);
                } else if (rightReadable == null) {
                    final var rightContent = ((StringExpression)right).getContent();
                    return Filter.createFilter(leftReadable, rightContent, operatorClass, converterRegistry);
                } else {
                    return Filter.createReadable(leftReadable, rightReadable, operatorClass, converterRegistry);
                }
            }
            
            default:
                throw new IllegalArgumentException("Invalid expression size: " + children.size());
        }
    }
    
    private static Readable<?> createReadable(Expression expression, IndexedReadableProvider indexedReadableProvider, IntValue indexValue, ConverterRegistry converterRegistry) {
        if (expression instanceof SequenceExpression) {
            return createExpressionReadable((SequenceExpression)expression, indexedReadableProvider, indexValue, converterRegistry);
        } else if (expression instanceof VariableExpression) {
            final var variableName = ((VariableExpression)expression).getContent();
            final var indexedReadable = indexedReadableProvider.getIndexedReadable(variableName);
            if (indexedReadable == null) {
                throw new IllegalArgumentException("Unknown variable: " + variableName);
            }
            return indexedReadable.createReadable(indexValue);
        } else {
            return null;
        }
    }
}
