package org.ribozyme.polygons;

import java.util.ArrayList;
import java.util.List;

import org.ribozyme.util.Line;
import org.ribozyme.util.ProjPoint;

public record Node(long c1, List<Node> children, ProjPoint point)
{
	public Node(List<Line> lines, ProjPoint point)
	{
		this(lines.stream().mapToLong(line -> line.latticePoints(point)).sum(), new ArrayList<>(), point);
	}
}
