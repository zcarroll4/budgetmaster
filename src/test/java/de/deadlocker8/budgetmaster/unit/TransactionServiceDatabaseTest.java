package de.deadlocker8.budgetmaster.unit;

import de.deadlocker8.budgetmaster.Main;
import de.deadlocker8.budgetmaster.accounts.AccountRepository;
import de.deadlocker8.budgetmaster.accounts.AccountType;
import de.deadlocker8.budgetmaster.filter.FilterConfiguration;
import de.deadlocker8.budgetmaster.integration.helpers.SeleniumTest;
import de.deadlocker8.budgetmaster.transactions.Transaction;
import de.deadlocker8.budgetmaster.transactions.TransactionRepository;
import de.deadlocker8.budgetmaster.transactions.TransactionService;
import de.deadlocker8.budgetmaster.utils.DateHelper;
import de.thecodelabs.utils.util.SystemUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Main.class)
@Import(TransactionServiceDatabaseTest.TestDatabaseConfiguration.class)
@ActiveProfiles("test")
@Transactional
class TransactionServiceDatabaseTest
{
	@TestConfiguration
	static class TestDatabaseConfiguration
	{
		@Value("classpath:repeating_with_tags.mv.db")
		private Resource databaseResource;

		@Bean
		@Primary
		public DataSource dataSource() throws IOException
		{
			final String folderName = databaseResource.getFile().getAbsolutePath().replace(".mv.db", "");
			String jdbcString = "jdbc:h2:/" + folderName + ";DB_CLOSE_ON_EXIT=TRUE";
			return DataSourceBuilder.create().username("sa").password("").url(jdbcString).driverClassName("org.h2.Driver").build();
		}
	}

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	DataSource dataSource;

	@Test
	void test_deleteAll()
	{
		transactionService.deleteAll();

		assertThat(transactionService.getRepository().findAll()).isEmpty();
	}

	@Test
	void test_getTransactionsForAccount_specificAccount()
	{
		LocalDate date1 = LocalDate.of(2020, 4, 30);
		FilterConfiguration filterConfiguration = new FilterConfiguration(true, true, true, true, true, null, null, "");

		List<Transaction> transactions = transactionService.getTransactionsForAccount(accountRepository.findByName("Second Account"), date1, LocalDate.now(), filterConfiguration);
		assertThat(transactions).hasSize(2);

		assertThat(transactions.get(0)).hasFieldOrPropertyWithValue("ID", 9);  // transfer
		assertThat(transactions.get(1)).hasFieldOrPropertyWithValue("ID", 37);  // normal transaction
	}

	@Test
	void test_getTransactionsForAccount_all()
	{
		LocalDate date1 = LocalDate.of(2020, 4, 30);
		FilterConfiguration filterConfiguration = new FilterConfiguration(true, true, true, true, true, null, null, "");

		List<Transaction> transactions = transactionService.getTransactionsForAccount(accountRepository.findAllByType(AccountType.ALL).get(0), date1, LocalDate.now(), filterConfiguration);
		assertThat(transactions).hasSize(8);
	}

	@Test
	void test_getTransactionsForAccountUntilDate()
	{
		LocalDate date1 = LocalDate.of(2020, 4, 30);
		LocalDate date2 = LocalDate.of(2020, 5, 20);
		FilterConfiguration filterConfiguration = new FilterConfiguration(true, true, true, true, true, null, null, "");

		List<Transaction> transactions = transactionService.getTransactionsForAccount(accountRepository.findByName("Default Account"), date1, date2, filterConfiguration);
		assertThat(transactions).hasSize(2);
	}

	@Test
	void test_getTransactionsForMonthAndYear()
	{
		FilterConfiguration filterConfiguration = new FilterConfiguration(true, true, true, true, true, null, null, "");

		List<Transaction> transactions = transactionService.getTransactionsForMonthAndYear(accountRepository.findByName("Default Account"), 6, 2021, false, filterConfiguration);
		assertThat(transactions).hasSize(1);
		assertThat(transactions.get(0).getDate())
				.isEqualTo(LocalDate.of(2021, 6, 30));
	}

	@Test
	void test_getTransactionsForMonthAndYear_CloseToMidnight()
	{
		try(MockedStatic<DateHelper> dateHelper = Mockito.mockStatic(DateHelper.class))
		{
			dateHelper.when(DateHelper::getCurrentDate).thenReturn(LocalDate.of(2021, 2, 5));

			FilterConfiguration filterConfiguration = new FilterConfiguration(true, true, true, true, true, null, null, "");

			List<Transaction> transactions = transactionService.getTransactionsForMonthAndYear(accountRepository.findByName("Default Account"), 6, 2021, false, filterConfiguration);
			assertThat(transactions).hasSize(1);
			assertThat(transactions.get(0).getDate())
					.isEqualTo(LocalDate.of(2021, 6, 30));
		}
	}
}
