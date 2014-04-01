package fantasyteam.sw2.collisions;

import fantasyteam.sw2.entities.Entity;
import java.util.List;

public class CollisionDetector
{
	public CollisionDetector()
	{

	}

	public void detectCollisions(List<Entity> moved_entities, List<Entity> all_entities)
	{
		for (int i=0; i<moved_entities.size(); i++)
		{
			Entity current_subject = moved_entities.get(i);
			BoundingBox subject_box = current_subject.getWorldBoundingPoints();

			for (int j=0; j<all_entities.size(); j++)
			{
				if (j != i)
				{
					Entity current_object = all_entities.get(j);
					BoundingBox object_box = current_object.getWorldBoundingPoints();
					boolean collision = false;
					
					if (subject_box.minX() >= object_box.minX() && subject_box.minX() <= object_box.maxX() && subject_box.minY() >= object_box.minY() && subject_box.minY() <= object_box.maxY())
					{
						collision = true;
					}

					if (subject_box.minX() >= object_box.minX() && subject_box.minX() <= object_box.maxX() && subject_box.maxY() >= object_box.minY() && subject_box.maxY() <= object_box.maxY())
					{
						collision = true;
					}

					if (subject_box.maxX() >= object_box.minX() && subject_box.maxX() <= object_box.maxX() && subject_box.minY() >= object_box.minY() && subject_box.minY() <= object_box.maxY())
					{
						collision = true;
					}

					if (subject_box.maxX() >= object_box.minX() && subject_box.maxX() <= object_box.maxX() && subject_box.maxY() >= object_box.minY() && subject_box.maxY() <= object_box.maxY())
					{
						collision = true;
					}

					if (collision)
					{
						System.out.println("Collision between " + current_object.getEntityType() + " and " + current_subject.getEntityType() + "!");
						current_subject.collide(current_object);
						current_object.collide(current_subject);
					}
				}
			}
		}
	}
}