package de.deadlocker8.budgetmaster.charts;

import de.thecodelabs.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class DefaultCharts
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCharts.class);

	public static final Chart CHART_DEFAULT = new Chart(null,
			getChartFromFile("charts/Default.js"),
			ChartType.CUSTOM, -1, ChartDisplayType.CUSTOM);

	private static final Chart CHART_ACCOUNT_SUM_PER_DAY = new Chart("charts.default.accountSumPerDay",
			getChartFromFile("charts/AccountSumPerDay.js"),
			ChartType.DEFAULT, 8, ChartDisplayType.LINE);

	private static final Chart CHART_INCOMES_AND_EXPENDITURES_PER_MONTH_BAR = new Chart("charts.default.incomesAndExpendituresPerMonthBar",
			getChartFromFile("charts/IncomesAndExpendituresPerMonthBar.js"),
			ChartType.DEFAULT, 8, ChartDisplayType.BAR);

	private static final Chart CHART_INCOMES_AND_EXPENDITURES_PER_MONTH_LINE = new Chart("charts.default.incomesAndExpendituresPerMonthLine",
			getChartFromFile("charts/IncomesAndExpendituresPerMonthLine.js"),
			ChartType.DEFAULT, 8, ChartDisplayType.LINE);

	private static final Chart CHART_INCOMES_AND_EXPENDITURES_BY_CATEGORY_BAR = new Chart("charts.default.incomesAndExpendituresByCategoryBar",
			getChartFromFile("charts/IncomesAndExpendituresByCategoryBar.js"),
			ChartType.DEFAULT, 3, ChartDisplayType.BAR);

	private static final Chart CHART_INCOMES_AND_EXPENDITURES_BY_CATEGORY_PIE = new Chart("charts.default.incomesAndExpendituresByCategoryPie",
			getChartFromFile("charts/IncomesAndExpendituresByCategoryPie.js"),
			ChartType.DEFAULT, 4, ChartDisplayType.PIE);

	private static final Chart CHART_INCOMES_AND_EXPENDITURES_PER_MONTH_BY_CATEGORIES = new Chart("charts.default.incomesAndExpendituresPerMonthByCategories",
			getChartFromFile("charts/IncomesAndExpendituresPerMonthByCategories.js"),
			ChartType.DEFAULT, 18, ChartDisplayType.BAR);

	private static final Chart CHART_REST_PER_MONTH = new Chart("charts.default.restPerMonth",
			getChartFromFile("charts/RestPerMonth.js"),
			ChartType.DEFAULT, 4, ChartDisplayType.BAR);

	private static final Chart CHART_INCOMES_AND_EXPENDITURES_PER_YEAR_BAR = new Chart("charts.default.incomesAndExpendituresPerYearBar",
			getChartFromFile("charts/IncomesAndExpendituresPerYearBar.js"),
			ChartType.DEFAULT, 3, ChartDisplayType.BAR);

	private DefaultCharts()
	{
	}

	public static List<Chart> getDefaultCharts()
	{
		List<Chart> charts = new ArrayList<>();
		charts.add(CHART_ACCOUNT_SUM_PER_DAY);
		charts.add(CHART_INCOMES_AND_EXPENDITURES_BY_CATEGORY_BAR);
		charts.add(CHART_INCOMES_AND_EXPENDITURES_PER_MONTH_LINE);
		charts.add(CHART_INCOMES_AND_EXPENDITURES_PER_MONTH_BAR);
		charts.add(CHART_INCOMES_AND_EXPENDITURES_BY_CATEGORY_PIE);
		charts.add(CHART_INCOMES_AND_EXPENDITURES_PER_MONTH_BY_CATEGORIES);
		charts.add(CHART_REST_PER_MONTH);
		charts.add(CHART_INCOMES_AND_EXPENDITURES_PER_YEAR_BAR);
		return charts;
	}

	private static String getChartFromFile(String filePath)
	{
		URL url = DefaultCharts.class.getClassLoader().getResource(filePath);
		if(url == null)
		{
			LOGGER.warn(MessageFormat.format("Couldn''t add default chart ''{0}'' due to missing file", filePath));
			return "";
		}

		try
		{
			return IOUtils.readURL(url);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		return "";
	}
}
