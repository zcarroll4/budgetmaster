package de.deadlocker8.budgetmaster.database;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.deadlocker8.budgetmaster.categories.Category;
import de.deadlocker8.budgetmaster.database.model.BackupDatabase;
import de.deadlocker8.budgetmaster.database.model.v4.BackupDatabase_v4;
import de.deadlocker8.budgetmaster.database.model.v5.BackupDatabase_v5;
import de.thecodelabs.utils.util.Localization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

public class DatabaseParser
{
	final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private final int MINIMUM_VERSION = 4;
	private final int LATEST_VERSION = 5;

	private final String jsonString;

	public DatabaseParser(String json)
	{
		this.jsonString = json;
	}

	public Database parseDatabaseFromJSON() throws IllegalArgumentException
	{
		int version = parseVersion();

		if(version < MINIMUM_VERSION)
		{
			throw new IllegalArgumentException(Localization.getString("error.database.import.version.too.old", version, MINIMUM_VERSION));
		}

		BackupDatabase importedDatabase = null;

		if(version == 4)
		{
			BackupDatabase_v4 parsedDatabase = new DatabaseParser_v4(jsonString).parseDatabaseFromJSON();
			LOGGER.debug(MessageFormat.format("Parsed database with {0} transactions, {1} categories, {2} accounts and {3} templates", parsedDatabase.getTransactions().size(), parsedDatabase.getCategories().size(), parsedDatabase.getAccounts().size(), parsedDatabase.getTemplates().size()));
			importedDatabase = parsedDatabase;
		}

		if(version == 5)
		{
			BackupDatabase_v5 parsedDatabase = new DatabaseParser_v5(jsonString).parseDatabaseFromJSON();
			LOGGER.debug(MessageFormat.format("Parsed database with {0} transactions, {1} categories, {2} accounts, {3} templates {4} charts and {5} images", parsedDatabase.getTransactions().size(), parsedDatabase.getCategories().size(), parsedDatabase.getAccounts().size(), parsedDatabase.getTemplates().size(), parsedDatabase.getCharts().size(), parsedDatabase.getImages().size()));
			importedDatabase = parsedDatabase;
		}

		if(importedDatabase == null)
		{
			throw new IllegalArgumentException(Localization.getString("error.database.import.unknown.version"));
		}

		return upgradeDatabase(importedDatabase);
	}

	private int parseVersion()
	{
		try
		{
			final JsonObject root = JsonParser.parseString(jsonString).getAsJsonObject();
			final String type = root.get("TYPE").getAsString();
			if(!type.equals(JSONIdentifier.BUDGETMASTER_DATABASE.toString()))
			{
				throw new IllegalArgumentException("JSON is not of type BUDGETMASTER_DATABASE");
			}

			int version = root.get("VERSION").getAsInt();
			LOGGER.info(MessageFormat.format("Parsing BudgetMaster database with version {0}", version));
			return version;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new IllegalArgumentException(Localization.getString("error.database.import.invalid.json"), e);
		}
	}

	private Database upgradeDatabase(BackupDatabase importedDatabase)
	{
		BackupDatabase upgradedDatabase = importedDatabase;
		while(upgradedDatabase.getVersion() < LATEST_VERSION)
		{
			LOGGER.debug(MessageFormat.format("Upgrading database from version {0} to {1}", upgradedDatabase.getVersion(), upgradedDatabase.getVersion() + 1));
			upgradedDatabase = upgradedDatabase.upgrade();
		}

		LOGGER.debug(MessageFormat.format("Converting database with version {0} to internal entities", upgradedDatabase.getVersion()));
		return upgradedDatabase.convert();
	}
}