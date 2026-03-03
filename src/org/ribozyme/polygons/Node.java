package org.ribozyme.polygons;

import java.util.ArrayList;
import java.util.List;

import org.ribozyme.util.Line;
import org.ribozyme.util.ProjPoint;

public record Node(long c1, List<Node> children, ProjPoint lattice, boolean optimal)
{
	public Node(List<Line> lines, ProjPoint lattice)
	{
		this(
				lines.stream().mapToLong(line -> line.latticePoints(lattice)).sum(),
				new ArrayList<>(),
				lattice,
				lines.stream().filter(line -> line.contains(lattice)).findAny().isPresent()
		);
	}
}
