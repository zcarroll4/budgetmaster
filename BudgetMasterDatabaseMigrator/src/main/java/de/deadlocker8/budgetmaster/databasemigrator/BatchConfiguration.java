package de.deadlocker8.budgetmaster.databasemigrator;

import de.deadlocker8.budgetmaster.databasemigrator.destination.StepNames;
import de.deadlocker8.budgetmaster.databasemigrator.destination.TableNames;
import de.deadlocker8.budgetmaster.databasemigrator.destination.account.DestinationAccount;
import de.deadlocker8.budgetmaster.databasemigrator.destination.account.DestinationAccountRepository;
import de.deadlocker8.budgetmaster.databasemigrator.destination.category.DestinationCategory;
import de.deadlocker8.budgetmaster.databasemigrator.destination.category.DestinationCategoryRepository;
import de.deadlocker8.budgetmaster.databasemigrator.destination.chart.DestinationChart;
import de.deadlocker8.budgetmaster.databasemigrator.destination.chart.DestinationChartRepository;
import de.deadlocker8.budgetmaster.databasemigrator.destination.hint.DestinationHint;
import de.deadlocker8.budgetmaster.databasemigrator.destination.hint.DestinationHintRepository;
import de.deadlocker8.budgetmaster.databasemigrator.destination.icon.DestinationIcon;
import de.deadlocker8.budgetmaster.databasemigrator.destination.icon.DestinationIconRepository;
import de.deadlocker8.budgetmaster.databasemigrator.destination.image.DestinationImage;
import de.deadlocker8.budgetmaster.databasemigrator.destination.image.DestinationImageRepository;
import de.deadlocker8.budgetmaster.databasemigrator.destination.repeating.DestinationRepeatingOption;
import de.deadlocker8.budgetmaster.databasemigrator.destination.repeating.DestinationRepeatingOptionRepository;
import de.deadlocker8.budgetmaster.databasemigrator.destination.repeating.end.*;
import de.deadlocker8.budgetmaster.databasemigrator.destination.repeating.modifier.*;
import de.deadlocker8.budgetmaster.databasemigrator.destination.report.DestinationReportColumn;
import de.deadlocker8.budgetmaster.databasemigrator.destination.report.DestinationReportColumnRepository;
import de.deadlocker8.budgetmaster.databasemigrator.destination.report.DestinationReportSettings;
import de.deadlocker8.budgetmaster.databasemigrator.destination.report.DestinationReportSettingsRepository;
import de.deadlocker8.budgetmaster.databasemigrator.destination.settings.DestinationSettings;
import de.deadlocker8.budgetmaster.databasemigrator.destination.settings.DestinationSettingsRepository;
import de.deadlocker8.budgetmaster.databasemigrator.destination.tag.*;
import de.deadlocker8.budgetmaster.databasemigrator.destination.transaction.DestinationTransaction;
import de.deadlocker8.budgetmaster.databasemigrator.destination.transaction.DestinationTransactionRepository;
import de.deadlocker8.budgetmaster.databasemigrator.destination.user.DestinationUser;
import de.deadlocker8.budgetmaster.databasemigrator.destination.user.DestinationUserRepository;
import de.deadlocker8.budgetmaster.databasemigrator.listener.GenericChunkListener;
import de.deadlocker8.budgetmaster.databasemigrator.listener.GenericJobListener;
import de.deadlocker8.budgetmaster.databasemigrator.listener.GenericStepListener;
import de.deadlocker8.budgetmaster.databasemigrator.steps.GenericDoNothingProcessor;
import de.deadlocker8.budgetmaster.databasemigrator.steps.GenericWriter;
import de.deadlocker8.budgetmaster.databasemigrator.steps.reader.*;
import de.deadlocker8.budgetmaster.databasemigrator.steps.reader.repeating.RepeatingOptionReader;
import de.deadlocker8.budgetmaster.databasemigrator.steps.reader.repeating.end.RepeatingEndAfterXTimesReader;
import de.deadlocker8.budgetmaster.databasemigrator.steps.reader.repeating.end.RepeatingEndDateReader;
import de.deadlocker8.budgetmaster.databasemigrator.steps.reader.repeating.end.RepeatingEndNeverReader;
import de.deadlocker8.budgetmaster.databasemigrator.steps.reader.repeating.end.RepeatingEndReader;
import de.deadlocker8.budgetmaster.databasemigrator.steps.reader.repeating.modifier.RepeatingModifierDaysReader;
import de.deadlocker8.budgetmaster.databasemigrator.steps.reader.repeating.modifier.RepeatingModifierMonthsReader;
import de.deadlocker8.budgetmaster.databasemigrator.steps.reader.repeating.modifier.RepeatingModifierReader;
import de.deadlocker8.budgetmaster.databasemigrator.steps.reader.repeating.modifier.RepeatingModifierYearsReader;
import de.deadlocker8.budgetmaster.databasemigrator.steps.reader.report.ReportColumnReader;
import de.deadlocker8.budgetmaster.databasemigrator.steps.reader.report.ReportSettingsReader;
import de.deadlocker8.budgetmaster.databasemigrator.steps.reader.tag.TagReader;
import de.deadlocker8.budgetmaster.databasemigrator.steps.reader.tag.TemplateTagReader;
import de.deadlocker8.budgetmaster.databasemigrator.steps.reader.tag.TransactionTagReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration
{
	final JobBuilderFactory jobBuilderFactory;
	final StepBuilderFactory stepBuilderFactory;

	final DataSource primaryDataSource;

	final DestinationImageRepository destinationImageRepository;
	final DestinationIconRepository destinationIconRepository;
	final DestinationCategoryRepository destinationCategoryRepository;
	final DestinationAccountRepository destinationAccountRepository;
	final DestinationChartRepository destinationChartRepository;
	final DestinationHintRepository destinationHintRepository;

	final DestinationRepeatingEndRepository destinationRepeatingEndRepository;
	final DestinationRepeatingEndAfterXTimesRepository destinationRepeatingEndAfterXTimesRepository;
	final DestinationRepeatingEndDateRepository destinationRepeatingEndDateRepository;
	final DestinationRepeatingEndNeverRepository destinationRepeatingEndNeverRepository;

	final DestinationRepeatingModifierRepository destinationRepeatingModifierRepository;
	final DestinationRepeatingModifierDaysRepository destinationRepeatingModifierDaysRepository;
	final DestinationRepeatingModifierMonthsRepository destinationRepeatingModifierMonthsRepository;
	final DestinationRepeatingModifierYearsRepository destinationRepeatingModifierYearsRepository;

	final DestinationRepeatingOptionRepository destinationRepeatingOptionRepository;

	final DestinationReportColumnRepository destinationReportColumnRepository;
	final DestinationReportSettingsRepository destinationReportSettingsRepository;

	final DestinationSettingsRepository destinationSettingsRepository;

	final DestinationTagRepository destinationTagRepository;
	final DestinationTemplateTagRepository destinationTemplateTagRepository;
	final DestinationTransactionTagRepository destinationTransactionTagRepository;

	final DestinationUserRepository destinationUserRepository;
	final DestinationTransactionRepository destinationTransactionRepository;

	public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, DataSource primaryDataSource, DestinationImageRepository destinationImageRepository, DestinationIconRepository destinationIconRepository, DestinationCategoryRepository destinationCategoryRepository, DestinationAccountRepository destinationAccountRepository, DestinationChartRepository destinationChartRepository, DestinationHintRepository destinationHintRepository, DestinationRepeatingEndRepository destinationRepeatingEndRepository, DestinationRepeatingEndAfterXTimesRepository destinationRepeatingEndAfterXTimesRepository, DestinationRepeatingEndDateRepository destinationRepeatingEndDateRepository, DestinationRepeatingEndNeverRepository destinationRepeatingEndNeverRepository, DestinationRepeatingModifierRepository destinationRepeatingModifierRepository, DestinationRepeatingModifierDaysRepository destinationRepeatingModifierDaysRepository, DestinationRepeatingModifierMonthsRepository destinationRepeatingModifierMonthsRepository, DestinationRepeatingModifierYearsRepository destinationRepeatingModifierYearsRepository, DestinationRepeatingOptionRepository destinationRepeatingOptionRepository, DestinationReportColumnRepository destinationReportColumnRepository, DestinationReportSettingsRepository destinationReportSettingsRepository, DestinationSettingsRepository destinationSettingsRepository, DestinationTagRepository destinationTagRepository, DestinationTemplateTagRepository destinationTemplateTagRepository, DestinationTransactionTagRepository destinationTransactionTagRepository, DestinationUserRepository destinationUserRepository, DestinationTransactionRepository destinationTransactionRepository)
	{
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
		this.primaryDataSource = primaryDataSource;

		this.destinationImageRepository = destinationImageRepository;
		this.destinationIconRepository = destinationIconRepository;
		this.destinationCategoryRepository = destinationCategoryRepository;
		this.destinationAccountRepository = destinationAccountRepository;
		this.destinationChartRepository = destinationChartRepository;
		this.destinationHintRepository = destinationHintRepository;

		this.destinationRepeatingEndRepository = destinationRepeatingEndRepository;
		this.destinationRepeatingEndAfterXTimesRepository = destinationRepeatingEndAfterXTimesRepository;
		this.destinationRepeatingEndDateRepository = destinationRepeatingEndDateRepository;
		this.destinationRepeatingEndNeverRepository = destinationRepeatingEndNeverRepository;

		this.destinationRepeatingModifierRepository = destinationRepeatingModifierRepository;
		this.destinationRepeatingModifierDaysRepository = destinationRepeatingModifierDaysRepository;
		this.destinationRepeatingModifierMonthsRepository = destinationRepeatingModifierMonthsRepository;
		this.destinationRepeatingModifierYearsRepository = destinationRepeatingModifierYearsRepository;

		this.destinationRepeatingOptionRepository = destinationRepeatingOptionRepository;

		this.destinationReportColumnRepository = destinationReportColumnRepository;
		this.destinationReportSettingsRepository = destinationReportSettingsRepository;

		this.destinationSettingsRepository = destinationSettingsRepository;

		this.destinationTagRepository = destinationTagRepository;
		this.destinationTemplateTagRepository = destinationTemplateTagRepository;
		this.destinationTransactionTagRepository = destinationTransactionTagRepository;

		this.destinationUserRepository = destinationUserRepository;
		this.destinationTransactionRepository = destinationTransactionRepository;
	}

	@Bean(name = "migrateJob")
	public Job createJob()
	{
		return jobBuilderFactory.get("Migrate from h2 to postgresql")
				.incrementer(new RunIdIncrementer())
				.start(createStepForImageMigration())
				.next(createStepForIconMigration())

				.next(createStepForCategoryMigration())
				.next(createStepForAccountMigration())

				.next(createStepForChartMigration())
				.next(createStepForHintMigration())

				// repeating end options
				.next(createStepForRepeatingEndMigration())
				.next(createStepForRepeatingEndAfterXTimesMigration())
				.next(createStepForRepeatingEndDateMigration())
				.next(createStepForRepeatingEndNeverMigration())

				// repeating modifiers
				.next(createStepForRepeatingModifierMigration())
				.next(createStepForRepeatingModifierDaysMigration())
				.next(createStepForRepeatingModifierMonthsMigration())
				.next(createStepForRepeatingModifierYearsMigration())

				.next(createStepForRepeatingOptionMigration())

				.next(createStepForReportColumnMigration())
				.next(createStepForReportSettingsMigration())

				.next(createStepForSettingsMigration())

				.next(createStepForTagMigration())
				.next(createStepForTemplateTagMigration())
				.next(createStepForTransactionTagMigration())

				.next(createStepForUserMigration())

				.next(createStepForTransactionMigration())

				.listener(new GenericJobListener())
				.build();
	}

	@Bean
	public Step createStepForImageMigration()
	{
		return stepBuilderFactory.get(StepNames.IMAGES)
				.<DestinationImage, DestinationImage>chunk(1)
				.reader(new ImageReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationImageRepository))
				.listener(new GenericChunkListener(TableNames.IMAGE))
				.listener(new GenericStepListener(TableNames.IMAGE))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForIconMigration()
	{
		return stepBuilderFactory.get(StepNames.ICONS)
				.<DestinationIcon, DestinationIcon>chunk(1)
				.reader(new IconReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationIconRepository))
				.listener(new GenericChunkListener(TableNames.ICON))
				.listener(new GenericStepListener(TableNames.ICON))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForCategoryMigration()
	{
		return stepBuilderFactory.get(StepNames.CATEGORIES)
				.<DestinationCategory, DestinationCategory>chunk(1)
				.reader(new CategoryReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationCategoryRepository))
				.listener(new GenericChunkListener(TableNames.CATEGORY))
				.listener(new GenericStepListener(TableNames.CATEGORY))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForAccountMigration()
	{
		return stepBuilderFactory.get(StepNames.ACCOUNTS)
				.<DestinationAccount, DestinationAccount>chunk(1)
				.reader(new AccountReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationAccountRepository))
				.listener(new GenericChunkListener(TableNames.ACCOUNT))
				.listener(new GenericStepListener(TableNames.ACCOUNT))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForChartMigration()
	{
		return stepBuilderFactory.get(StepNames.CHARTS)
				.<DestinationChart, DestinationChart>chunk(1)
				.reader(new ChartReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationChartRepository))
				.listener(new GenericChunkListener(TableNames.CHART))
				.listener(new GenericStepListener(TableNames.CHART))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForHintMigration()
	{
		return stepBuilderFactory.get(StepNames.HINTS)
				.<DestinationHint, DestinationHint>chunk(1)
				.reader(new HintReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationHintRepository))
				.listener(new GenericChunkListener(TableNames.HINT))
				.listener(new GenericStepListener(TableNames.HINT))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForRepeatingEndMigration()
	{
		return stepBuilderFactory.get(StepNames.REPEATING_ENDS)
				.<DestinationRepeatingEnd, DestinationRepeatingEnd>chunk(1)
				.reader(new RepeatingEndReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationRepeatingEndRepository))
				.listener(new GenericChunkListener(TableNames.REPEATING_END))
				.listener(new GenericStepListener(TableNames.REPEATING_END))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForRepeatingEndAfterXTimesMigration()
	{
		return stepBuilderFactory.get(StepNames.REPEATING_END_AFTER_X_TIMES)
				.<DestinationRepeatingEndAfterXTimes, DestinationRepeatingEndAfterXTimes>chunk(1)
				.reader(new RepeatingEndAfterXTimesReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationRepeatingEndAfterXTimesRepository))
				.listener(new GenericChunkListener(TableNames.REPEATING_END_AFTER_X_TIMES))
				.listener(new GenericStepListener(TableNames.REPEATING_END_AFTER_X_TIMES))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForRepeatingEndDateMigration()
	{
		return stepBuilderFactory.get(StepNames.REPEATING_END_DATE)
				.<DestinationRepeatingEndDate, DestinationRepeatingEndDate>chunk(1)
				.reader(new RepeatingEndDateReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationRepeatingEndDateRepository))
				.listener(new GenericChunkListener(TableNames.REPEATING_END_DATE))
				.listener(new GenericStepListener(TableNames.REPEATING_END_DATE))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForRepeatingEndNeverMigration()
	{
		return stepBuilderFactory.get(StepNames.REPEATING_END_NEVER)
				.<DestinationRepeatingEndNever, DestinationRepeatingEndNever>chunk(1)
				.reader(new RepeatingEndNeverReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationRepeatingEndNeverRepository))
				.listener(new GenericChunkListener(TableNames.REPEATING_END_NEVER))
				.listener(new GenericStepListener(TableNames.REPEATING_END_NEVER))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForRepeatingModifierMigration()
	{
		return stepBuilderFactory.get(StepNames.REPEATING_MODIFIERS)
				.<DestinationRepeatingModifier, DestinationRepeatingModifier>chunk(1)
				.reader(new RepeatingModifierReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationRepeatingModifierRepository))
				.listener(new GenericChunkListener(TableNames.REPEATING_MODIFIER))
				.listener(new GenericStepListener(TableNames.REPEATING_MODIFIER))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForRepeatingModifierDaysMigration()
	{
		return stepBuilderFactory.get(StepNames.REPEATING_MODIFIER_DAYS)
				.<DestinationRepeatingModifierDays, DestinationRepeatingModifierDays>chunk(1)
				.reader(new RepeatingModifierDaysReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationRepeatingModifierDaysRepository))
				.listener(new GenericChunkListener(TableNames.REPEATING_MODIFIER_DAYS))
				.listener(new GenericStepListener(TableNames.REPEATING_MODIFIER_DAYS))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForRepeatingModifierMonthsMigration()
	{
		return stepBuilderFactory.get(StepNames.REPEATING_MODIFIER_MONTHS)
				.<DestinationRepeatingModifierMonths, DestinationRepeatingModifierMonths>chunk(1)
				.reader(new RepeatingModifierMonthsReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationRepeatingModifierMonthsRepository))
				.listener(new GenericChunkListener(TableNames.REPEATING_MODIFIER_MONTHS))
				.listener(new GenericStepListener(TableNames.REPEATING_MODIFIER_MONTHS))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForRepeatingModifierYearsMigration()
	{
		return stepBuilderFactory.get(StepNames.REPEATING_MODIFIER_YEARS)
				.<DestinationRepeatingModifierYears, DestinationRepeatingModifierYears>chunk(1)
				.reader(new RepeatingModifierYearsReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationRepeatingModifierYearsRepository))
				.listener(new GenericChunkListener(TableNames.REPEATING_MODIFIER_YEARS))
				.listener(new GenericStepListener(TableNames.REPEATING_MODIFIER_YEARS))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForRepeatingOptionMigration()
	{
		return stepBuilderFactory.get(StepNames.REPEATING_OPTIONS)
				.<DestinationRepeatingOption, DestinationRepeatingOption>chunk(1)
				.reader(new RepeatingOptionReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationRepeatingOptionRepository))
				.listener(new GenericChunkListener(TableNames.REPEATING_OPTION))
				.listener(new GenericStepListener(TableNames.REPEATING_OPTION))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForReportColumnMigration()
	{
		return stepBuilderFactory.get(StepNames.REPORT_COLUMNS)
				.<DestinationReportColumn, DestinationReportColumn>chunk(1)
				.reader(new ReportColumnReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationReportColumnRepository))
				.listener(new GenericChunkListener(TableNames.REPORT_COLUMN))
				.listener(new GenericStepListener(TableNames.REPORT_COLUMN))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForReportSettingsMigration()
	{
		return stepBuilderFactory.get(StepNames.REPORT_SETTINGS)
				.<DestinationReportSettings, DestinationReportSettings>chunk(1)
				.reader(new ReportSettingsReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationReportSettingsRepository))
				.listener(new GenericChunkListener(TableNames.REPORT_SETTINGS))
				.listener(new GenericStepListener(TableNames.REPORT_SETTINGS))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForSettingsMigration()
	{
		return stepBuilderFactory.get(StepNames.SETTINGS)
				.<DestinationSettings, DestinationSettings>chunk(1)
				.reader(new SettingsReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationSettingsRepository))
				.listener(new GenericChunkListener(TableNames.SETTINGS))
				.listener(new GenericStepListener(TableNames.SETTINGS))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForTagMigration()
	{
		return stepBuilderFactory.get(StepNames.TAGS)
				.<DestinationTag, DestinationTag>chunk(1)
				.reader(new TagReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationTagRepository))
				.listener(new GenericChunkListener(TableNames.TAG))
				.listener(new GenericStepListener(TableNames.TAG))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForTemplateTagMigration()
	{
		return stepBuilderFactory.get(StepNames.TEMPLATE_TAGS)
				.<DestinationTemplateTag, DestinationTemplateTag>chunk(1)
				.reader(new TemplateTagReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationTemplateTagRepository))
				.listener(new GenericChunkListener(TableNames.TEMPLATE_TAGS))
				.listener(new GenericStepListener(TableNames.TEMPLATE_TAGS))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForTransactionTagMigration()
	{
		return stepBuilderFactory.get(StepNames.TRANSACTION_TAGS)
				.<DestinationTransactionTag, DestinationTransactionTag>chunk(1)
				.reader(new TransactionTagReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationTransactionTagRepository))
				.listener(new GenericChunkListener(TableNames.TRANSACTION_TAGS))
				.listener(new GenericStepListener(TableNames.TRANSACTION_TAGS))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForUserMigration()
	{
		return stepBuilderFactory.get(StepNames.USER)
				.<DestinationUser, DestinationUser>chunk(1)
				.reader(new UserReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationUserRepository))
				.listener(new GenericChunkListener(TableNames.USER_SOURCE))
				.listener(new GenericStepListener(TableNames.USER_SOURCE))
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step createStepForTransactionMigration()
	{
		return stepBuilderFactory.get(StepNames.TRANSACTIONS)
				.<DestinationTransaction, DestinationTransaction>chunk(1)
				.reader(new TransactionReader(primaryDataSource))
				.processor(new GenericDoNothingProcessor<>())
				.writer(new GenericWriter<>(destinationTransactionRepository))
				.listener(new GenericChunkListener(TableNames.TRANSACTION))
				.listener(new GenericStepListener(TableNames.TRANSACTION))
				.allowStartIfComplete(true)
				.build();
	}
}
