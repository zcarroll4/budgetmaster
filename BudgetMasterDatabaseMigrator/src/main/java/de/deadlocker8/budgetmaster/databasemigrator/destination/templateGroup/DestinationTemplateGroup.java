package de.deadlocker8.budgetmaster.databasemigrator.destination.templateGroup;


import de.deadlocker8.budgetmaster.databasemigrator.CustomIdGenerator;
import de.deadlocker8.budgetmaster.databasemigrator.destination.ProvidesID;
import de.deadlocker8.budgetmaster.databasemigrator.destination.TableNames;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = TableNames.TEMPLATE_GROUP)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DestinationTemplateGroup implements ProvidesID
{
	@Id
	@GeneratedValue(generator = "custom_generator")
	@GenericGenerator(name = "custom_generator", strategy = CustomIdGenerator.GENERATOR)
	private Integer ID;

	private String name;

	private Integer type;
}
