import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;
import java.util.*;
import java.lang.*;

class CollisionDetector
{
	CollisionDetector()
	{

	}

	public void detectCollisions(Vector<Entity> moved_entities, Vector<Entity> all_entities)
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