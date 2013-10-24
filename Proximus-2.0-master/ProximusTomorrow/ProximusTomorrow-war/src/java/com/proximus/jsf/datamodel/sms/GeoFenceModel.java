package com.proximus.jsf.datamodel.sms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.primefaces.model.map.*;

public class GeoFenceModel implements MapModel, Serializable
{

    private List<Marker> markers;
    private List<Polyline> polylines;
    private List<Polygon> polygons;
    private List<Circle> circles;
    private List<Rectangle> rectangles;
    private final static String MARKER_ID_PREFIX = "marker";
    private final static String POLYLINE_ID_PREFIX = "polyline_";
    private final static String POLYGON_ID_PREFIX = "polygon_";
    private final static String CIRCLE_ID_PREFIX = "circle_";
    private final static String RECTANGLE_ID_PREFIX = "rectangle_";

    public GeoFenceModel()
    {
        markers = new ArrayList<Marker>();
        polylines = new ArrayList<Polyline>();
        polygons = new ArrayList<Polygon>();
        circles = new ArrayList<Circle>();
        rectangles = new ArrayList<Rectangle>();
    }

    @Override
    public List<Marker> getMarkers()
    {
        return markers;
    }

    @Override
    public List<Polyline> getPolylines()
    {
        return polylines;
    }

    @Override
    public List<Polygon> getPolygons()
    {
        return polygons;
    }

    @Override
    public List<Circle> getCircles()
    {
        return circles;
    }

    @Override
    public List<Rectangle> getRectangles()
    {
        return rectangles;
    }

    @Override
    public void addOverlay(Overlay overlay)
    {
        if (overlay instanceof Marker)
        {
            overlay.setId(MARKER_ID_PREFIX + UUID.randomUUID().toString());
            markers.add((Marker) overlay);
        } else if (overlay instanceof Polyline)
        {
            overlay.setId(POLYLINE_ID_PREFIX + UUID.randomUUID().toString());
            polylines.add((Polyline) overlay);
        } else if (overlay instanceof Polygon)
        {
            overlay.setId(POLYGON_ID_PREFIX + UUID.randomUUID().toString());
            polygons.add((Polygon) overlay);
        } else if (overlay instanceof Circle)
        {
            overlay.setId(CIRCLE_ID_PREFIX + UUID.randomUUID().toString());
            circles.add((Circle) overlay);
        } else if (overlay instanceof Rectangle)
        {
            overlay.setId(RECTANGLE_ID_PREFIX + UUID.randomUUID().toString());
            rectangles.add((Rectangle) overlay);
        }
    }

    @Override
    public Overlay findOverlay(String id)
    {
        List list = null;

        if (id.startsWith(MARKER_ID_PREFIX))
        {
            list = markers;
        } else if (id.startsWith(POLYLINE_ID_PREFIX))
        {
            list = polylines;
        } else if (id.startsWith(POLYGON_ID_PREFIX))
        {
            list = polygons;
        } else if (id.startsWith(CIRCLE_ID_PREFIX))
        {
            list = circles;
        } else if (id.startsWith(RECTANGLE_ID_PREFIX))
        {
            list = rectangles;
        }

        for (Iterator iterator = list.iterator(); iterator.hasNext();)
        {
            Overlay overlay = (Overlay) iterator.next();

            if (overlay.getId().equals(id))
            {
                return overlay;
            }
        }

        return null;
    }

    public void clearMarkers()
    {
        this.markers.clear();
    }

    public void clearCircles()
    {
        this.circles.clear();
    }

    public void clearPolylines()
    {
        this.polylines.clear();
    }

    public void clearPolygons()
    {
        this.polygons.clear();
    }

    public void clearRectangles()
    {
        this.rectangles.clear();
    }
}