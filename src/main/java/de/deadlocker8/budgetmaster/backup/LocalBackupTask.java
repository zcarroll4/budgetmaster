package de.deadlocker8.budgetmaster.backup;

import de.deadlocker8.budgetmaster.database.DatabaseService;
import de.deadlocker8.budgetmaster.settings.Settings;
import de.deadlocker8.budgetmaster.settings.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

public class LocalBackupTask extends BackupTask
{
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalBackupTask.class);

	private final DatabaseService databaseService;

	public LocalBackupTask(DatabaseService databaseService, SettingsService settingsService)
	{
		super(databaseService, settingsService);
		this.databaseService = databaseService;
	}

	@Override
	public void run()
	{
		LOGGER.debug(MessageFormat.format("Starting backup with strategy \"{0}\"", AutoBackupStrategy.LOCAL));
		databaseService.backupDatabase(getBackupFolder());
		LOGGER.debug("Backup DONE");
	}

	@Override
	public void cleanup(Settings previousSettings, Settings newSettings)
	{
		if(!needsCleanup(previousSettings, newSettings))
		{
			return;
		}
	}

	@Override
	protected boolean needsCleanup(Settings previousSettings, Settings newSettings)
	{
		// nothing to do here, file system copies could remain inside the backup folder
		return false;
	}
}
