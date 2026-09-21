package vn.edu.hcmute.bt09graphql.config;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.math.BigDecimal;
import java.util.Locale;

@Configuration
public class GraphQlConfig {

    public static final GraphQLScalarType BIG_DECIMAL_SCALAR = GraphQLScalarType.newScalar()
            .name("BigDecimal")
            .description("Built-in java.math.BigDecimal scalar")
            .coercing(new Coercing<BigDecimal, Object>() {
                @Override
                public Object serialize(Object dataFetcherResult, GraphQLContext graphQLContext, Locale locale) throws CoercingSerializeException {
                    if (dataFetcherResult instanceof BigDecimal) {
                        return dataFetcherResult;
                    }
                    if (dataFetcherResult instanceof Number) {
                        return new BigDecimal(dataFetcherResult.toString());
                    }
                    if (dataFetcherResult instanceof String) {
                        try {
                            return new BigDecimal((String) dataFetcherResult);
                        } catch (NumberFormatException e) {
                            throw new CoercingSerializeException("Unable to serialize " + dataFetcherResult + " to BigDecimal", e);
                        }
                    }
                    throw new CoercingSerializeException("Expected a BigDecimal or Number but was " + (dataFetcherResult != null ? dataFetcherResult.getClass().getName() : "null"));
                }

                @Override
                public BigDecimal parseValue(Object input, GraphQLContext graphQLContext, Locale locale) throws CoercingParseValueException {
                    if (input instanceof BigDecimal) {
                        return (BigDecimal) input;
                    }
                    if (input instanceof Number) {
                        return new BigDecimal(input.toString());
                    }
                    if (input instanceof String) {
                        try {
                            return new BigDecimal((String) input);
                        } catch (NumberFormatException e) {
                            throw new CoercingParseValueException("Unable to parse value " + input + " to BigDecimal", e);
                        }
                    }
                    throw new CoercingParseValueException("Expected a BigDecimal or Number but was " + (input != null ? input.getClass().getName() : "null"));
                }

                @Override
                public BigDecimal parseLiteral(Value<?> input, CoercedVariables variables, GraphQLContext graphQLContext, Locale locale) throws CoercingParseLiteralException {
                    if (input instanceof FloatValue) {
                        return ((FloatValue) input).getValue();
                    }
                    if (input instanceof IntValue) {
                        return new BigDecimal(((IntValue) input).getValue());
                    }
                    if (input instanceof StringValue) {
                        try {
                            return new BigDecimal(((StringValue) input).getValue());
                        } catch (NumberFormatException e) {
                            throw new CoercingParseLiteralException("Unable to parse literal " + input + " to BigDecimal", e);
                        }
                    }
                    throw new CoercingParseLiteralException("Expected FloatValue, IntValue, or StringValue AST literal, but was " + input.getClass().getSimpleName());
                }
            })
            .build();

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder.scalar(BIG_DECIMAL_SCALAR);
    }
}
