package com.adaptris.core.cassandra.params;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.AutoPopulated;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.ServiceException;
import com.adaptris.core.services.jdbc.StatementParameter;
import com.adaptris.core.services.jdbc.StatementParameterCollection;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * {@link CassandraParameterApplicator} implementation that allows referencing by name.
 *
 * <p>
 * Using a {@link NamedParameterApplicator} implementation means that you can modify your CQL statement to reference named statement
 * parameters making it no longer depending on declaration order.
 * </p>
 * <p>
 * For instance:
 *
 * <pre>
 * {@code SELECT * FROM mytable WHERE field1=#param1 AND field2=#param2 AND field3=#param3 AND field4=#param4 AND field5=#param5}
 * </pre>
 * If you then named your statement parameters as {@code param1, param2, param3, param4, param5} using
 * {@link StatementParameter#setName(String)} then the order of parameters as they appear in configuration no longer matters.
 * </p>
 *
 * @author amcgrath
 * @config cassandra-named-parameter-applicator
 *
 */
@XStreamAlias("cassandra-named-parameter-applicator")
@AdapterComponent
@ComponentProfile(summary = "Helps to use named parameters in CQL statements", tag = "cassandra")
public class NamedParameterApplicator extends AbstractCassandraParameterApplicator {

  protected transient Logger log = LoggerFactory.getLogger(this.getClass().getName());

  public static final String DEFAULT_PARAM_NAME_PREFIX = "#";
  public static final String DEFAULT_PARAM_NAME_REGEX = "#\\w*";

  @NotBlank
  @AutoPopulated
  private String parameterNamePrefix;

  @NotBlank
  @AutoPopulated
  private String parameterNameRegex;

  public NamedParameterApplicator() {
    setParameterNamePrefix(DEFAULT_PARAM_NAME_PREFIX);
    setParameterNameRegex(DEFAULT_PARAM_NAME_REGEX);
  }

  @Override
  public BoundStatement applyParameters(Session session, AdaptrisMessage message, StatementParameterCollection parameters, String statement) throws ServiceException {
    String formattedStatement = statement.replaceAll(getParameterNameRegex(), "?");

    PreparedStatement preparedStatement;
    try {
      preparedStatement = prepareStatement(session, formattedStatement);
    } catch (Exception e) {
      throw new ServiceException(e);
    }
    BoundStatement boundStatement = new BoundStatement(preparedStatement);

    Matcher matcher = Pattern.compile(getParameterNameRegex()).matcher(statement);

    ArrayList<Object> foundParameters = new ArrayList<>();
    while (matcher.find()) {
      String parameterName = matcher.group();
      StatementParameter statementParameter = (StatementParameter) parameters.getParameterByName(parameterName.substring(getParameterNamePrefix().length()));
      if (statementParameter == null) {
        throw new ServiceException("Parameter " + parameterName + ", cannot be found in the configured parameter list");
      }

      foundParameters.add(ParameterHelper.convertToQueryClass(statementParameter.getQueryValue(message), statementParameter.getQueryClass()));
    }

    return boundStatement.bind(foundParameters.toArray());
  }

  public String getParameterNamePrefix() {
    return parameterNamePrefix;
  }

  /**
   * Set the parameter name prefix.
   *
   * @param str the parameter name prefix, defaults to {@value #DEFAULT_PARAM_NAME_PREFIX}
   */
  public void setParameterNamePrefix(String str) {
    parameterNamePrefix = str;
  }

  public String getParameterNameRegex() {
    return parameterNameRegex;
  }

  /**
   * Set the parameter name regular expression.
   *
   * @param regex the parameter name regex, defaults to {@value #DEFAULT_PARAM_NAME_REGEX}
   */
  public void setParameterNameRegex(String regex) {
    parameterNameRegex = regex;
  }
}
