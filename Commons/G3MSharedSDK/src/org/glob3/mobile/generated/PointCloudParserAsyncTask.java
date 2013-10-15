package org.glob3.mobile.generated; 
public class PointCloudParserAsyncTask extends GAsyncTask
{
  private G3MContext _context;

  private MeshRenderer _meshRenderer;
  public final URL _url;
  private IByteBuffer _buffer;
  private final float _pointSize;
  private MeshLoadListener _listener;
  private final boolean _deleteListener;
  private final boolean _isBSON;

  private Mesh _mesh;


  public PointCloudParserAsyncTask(MeshRenderer meshRenderer, URL url, IByteBuffer buffer, float pointSize, MeshLoadListener listener, boolean deleteListener, boolean isBSON, G3MContext context)
  {
     _meshRenderer = meshRenderer;
     _url = url;
     _buffer = buffer;
     _pointSize = pointSize;
     _listener = listener;
     _deleteListener = deleteListener;
     _isBSON = isBSON;
     _context = context;
     _mesh = null;
  }

  public final void runInBackground(G3MContext context)
  {
    final JSONBaseObject jsonBaseObject = _isBSON ? BSONParser.parse(_buffer) : IJSONParser.instance().parse(_buffer);
    /*                                         */
    /*                                         */

    if (jsonBaseObject != null)
    {
      final JSONObject jsonObject = jsonBaseObject.asObject();
      if (jsonObject == null)
      {
        ILogger.instance().logError("Invalid format for PointCloud");
      }
      else
      {

//      "size":48
//      ,"bounds":{
//        "lower":[7.1489565398362656,46.34361696257866,1169.652386999709]
//        ,"upper":[7.171002826337067,46.3559190937692,1381.608242625843]
//      }
//      ,"averagePoint":[7.159396937202478,46.34958505936779,1229.5747544618582]
//      ,"points":[ ]
//      ,"colors":[ ]

        final int size = (int) jsonObject.getAsNumber("size", -1);
        if (size <= 0)
        {
          ILogger.instance().logError("Invalid size for PointCloud");
        }
        else
        {
          Geodetic3D averagePoint = null;

          final JSONArray jsonAveragePoint = jsonObject.getAsArray("averagePoint");
          if ((jsonAveragePoint != null) && (jsonAveragePoint.size() == 3))
          {
            final double lonInDegrees = jsonAveragePoint.getAsNumber(0, 0);
            final double latInDegrees = jsonAveragePoint.getAsNumber(1, 0);
            final double height = jsonAveragePoint.getAsNumber(2, 0);

            averagePoint = new Geodetic3D(Angle.fromDegrees(latInDegrees), Angle.fromDegrees(lonInDegrees), height);
          }
          else
          {
            ILogger.instance().logError("Invalid averagePoint for PointCloud");
          }

          final JSONArray jsonPoints = jsonObject.getAsArray("points");
          if ((jsonPoints == null) || (size *3 != jsonPoints.size()))
          {
            ILogger.instance().logError("Invalid points for PointCloud");
          }
          else
          {
            FloatBufferBuilderFromGeodetic vertices = (averagePoint == null) ? FloatBufferBuilderFromGeodetic.builderWithFirstVertexAsCenter(_context.getPlanet()) : FloatBufferBuilderFromGeodetic.builderWithGivenCenter(_context.getPlanet(), averagePoint);
            /*                 */
            /*                 */

            for (int i = 0; i < size *3; i += 3)
            {
              final double lonInDegrees = jsonPoints.getAsNumber(i, 0);
              final double latInDegrees = jsonPoints.getAsNumber(i + 1, 0);
              final double height = jsonPoints.getAsNumber(i + 2, 0);

              vertices.add(Angle.fromDegrees(latInDegrees), Angle.fromDegrees(lonInDegrees), height);
            }

            final JSONArray jsonColors = jsonObject.getAsArray("colors");
            if (jsonColors == null)
            {
              _mesh = new DirectMesh(GLPrimitive.points(), true, vertices.getCenter(), vertices.create(), 1, _pointSize, null, null, 1, false); // colors.create(), -  flatColor, -  lineWidth

            }
            else
            {
              FloatBufferBuilderFromColor colors = new FloatBufferBuilderFromColor();

              final int colorsSize = jsonColors.size();
              for (int i = 0; i < colorsSize; i += 3)
              {
                final int red = (int) jsonColors.getAsNumber(i, 0);
                final int green = (int) jsonColors.getAsNumber(i + 1, 0);
                final int blue = (int) jsonColors.getAsNumber(i + 2, 0);

                colors.addBase255(red, green, blue, 1);
              }

              _mesh = new DirectMesh(GLPrimitive.points(), true, vertices.getCenter(), vertices.create(), 1, _pointSize, null, colors.create(), 1, false); // flatColor, -  lineWidth
            }

//            const int primitive,
//            bool owner,
//            const Vector3D& center,
//            IFloatBuffer* vertices,
//            float lineWidth,
//            float pointSize,
//            Color* flatColor = NULL,
//            IFloatBuffer* colors = NULL,
//            const float colorsIntensity = 0.0f,
//            bool depthTest = true,
//            IFloatBuffer* normals = NULL

          }

          if (averagePoint != null)
             averagePoint.dispose();
        }
      }

      if (jsonBaseObject != null)
         jsonBaseObject.dispose();
    }

    if (_buffer != null)
       _buffer.dispose();
    _buffer = null;
  }

  public void dispose()
  {
    if (_deleteListener)
    {
      if (_listener != null)
         _listener.dispose();
    }
    if (_buffer != null)
       _buffer.dispose();
  }

  public final void onPostExecute(G3MContext context)
  {
    if (_mesh == null)
    {
      ILogger.instance().logError("Error parsing PointCloud from \"%s\"", _url.getPath());
    }
    else
    {
      if (_listener != null)
      {
        _listener.onBeforeAddMesh(_mesh);
      }

      ILogger.instance().logInfo("Adding PointCloud Mesh to _meshRenderer");
      _meshRenderer.addMesh(_mesh);

      if (_listener != null)
      {
        _listener.onAfterAddMesh(_mesh);
      }

      _mesh = null;
    }
  }

}