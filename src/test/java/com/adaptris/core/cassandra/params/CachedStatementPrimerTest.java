package com.adaptris.core.cassandra.params;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.adaptris.interlok.util.Closer;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;

public class CachedStatementPrimerTest {

  @Mock
  private CqlSession mockSession;

  @Mock
  private PreparedStatement mockPreparedStatement;

  private AutoCloseable openMocks;

  private CachedStatementPrimer statementPrimer;

  @BeforeEach
  public void setUp() throws Exception {
    statementPrimer = new CachedStatementPrimer();
    openMocks = MockitoAnnotations.openMocks(this);

    when(mockSession.prepare(anyString())).thenReturn(mockPreparedStatement);
  }

  @AfterEach
  public void tearDown() throws Exception {
    Closer.closeQuietly(openMocks);
  }

  @Test
  public void testNewStatementAdded() throws Exception {
    String stringStatement = "select * from myTable";

    assertEquals(0, statementPrimer.getStatements().size());

    statementPrimer.prepareStatement(mockSession, stringStatement);

    assertEquals(1, statementPrimer.getStatements().size());
    assertEquals(stringStatement, statementPrimer.getStatements().get(0).getStringStatement());
  }

  @Test
  public void testDuplicateNotAdded() throws Exception {
    String stringStatement = "select * from myTable";

    assertEquals(0, statementPrimer.getStatements().size());

    statementPrimer.prepareStatement(mockSession, stringStatement);

    assertEquals(1, statementPrimer.getStatements().size());
    assertEquals(stringStatement, statementPrimer.getStatements().get(0).getStringStatement());

    statementPrimer.prepareStatement(mockSession, stringStatement);
    statementPrimer.prepareStatement(mockSession, stringStatement);

    assertEquals(1, statementPrimer.getStatements().size());
    assertEquals(stringStatement, statementPrimer.getStatements().get(0).getStringStatement());
  }

  @Test
  public void testNewMultipleStatementsAdded() throws Exception {
    String stringStatement = "select * from myTable";
    String stringStatement2 = "select * from myTable2";

    assertEquals(0, statementPrimer.getStatements().size());

    statementPrimer.prepareStatement(mockSession, stringStatement);

    assertEquals(1, statementPrimer.getStatements().size());
    assertEquals(stringStatement, statementPrimer.getStatements().get(0).getStringStatement());

    statementPrimer.prepareStatement(mockSession, stringStatement2);

    assertEquals(2, statementPrimer.getStatements().size());
  }

  @Test
  public void testCacheLimit() throws Exception {
    statementPrimer.setCacheLimit(1);

    String stringStatement = "select * from myTable";
    String stringStatement2 = "select * from myTable2";

    assertEquals(0, statementPrimer.getStatements().size());

    statementPrimer.prepareStatement(mockSession, stringStatement);

    assertEquals(1, statementPrimer.getStatements().size());
    assertEquals(stringStatement, statementPrimer.getStatements().get(0).getStringStatement());

    statementPrimer.prepareStatement(mockSession, stringStatement2);

    assertEquals(1, statementPrimer.getStatements().size());
    assertEquals(stringStatement2, statementPrimer.getStatements().get(0).getStringStatement());
  }

}
