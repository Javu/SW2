class BoundingBox{
	private int min_x;
	private int min_y;
	private int max_x;
	private int max_y;

	BoundingBox()
	{
		min_x = 0;
		min_y = 0;
		max_x = 0;
		max_y = 0;
	}

	BoundingBox(int new_min_x, int new_min_y, int new_max_x, int new_max_y)
	{	
		min_x = new_min_x;
		min_y = new_min_y;
		max_x = new_max_x;
		max_y = new_max_y;

		if (max_x < min_x)
		{
			max_x = min_x;
		}

		if (max_y < min_y)
		{
			max_y = min_y;
		}
	}

	public int minX()
	{
		return min_x;
	}

	public int minY()
	{
		return min_y;
	}

	public int maxX()
	{
		return max_x;
	}

	public int maxY()
	{
		return max_y;
	}

	public boolean minX(int new_min_x)
	{
		if (new_min_x <= max_x)
		{
			min_x = new_min_x;
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean minY(int new_min_y)
	{
		if (new_min_y <= max_y)
		{
			min_y = new_min_y;
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean maxX(int new_max_x)
	{
		if (new_max_x >= min_x)
		{
			max_x = new_max_x;
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean maxY(int new_max_y)
	{
		if (new_max_y >= min_y)
		{
			max_y = new_max_y;
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean setPoints(int new_min_x, int new_max_x, int new_min_y, int new_max_y)
	{
		if (new_min_x < new_max_x && new_min_y < new_max_y)
		{
			min_x = new_min_x;
			max_x = new_max_x;
			min_y = new_min_y;
			max_y = new_max_y;
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean equals(BoundingBox other)
	{
		if (min_x == other.minX() && min_y == other.minY() && max_x == other.maxX() && max_y == other.maxY())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}