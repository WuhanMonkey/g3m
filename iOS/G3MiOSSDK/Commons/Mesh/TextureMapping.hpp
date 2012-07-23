//
//  TextureMapping.hpp
//  G3MiOSSDK
//
//  Created by José Miguel S N on 12/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#ifndef G3MiOSSDK_TextureMapping_hpp
#define G3MiOSSDK_TextureMapping_hpp

#include "MutableVector2D.hpp"
#include <vector>

class RenderContext;


class TextureMapping
{
private:
  const int          _textureId;
  const float const* _texCoords;
  MutableVector2D    _translation, _scale;
  
public:
  
  TextureMapping(int textureId,
                 float texCoords[]) :
  _textureId(textureId),
  _texCoords(texCoords)
  {
    MutableVector2D t(0, 0);
    _translation = t;
    MutableVector2D s(1, 1);
    _scale = s;
  }
  
  TextureMapping(int textureId,
                 std::vector<MutableVector2D> texCoords);
  
  void setTranslationAndScale(const Vector2D& translation,
                              const Vector2D& scale){
    _translation = translation.asMutableVector2D();
    _scale       = scale.asMutableVector2D();
  }
  
  ~TextureMapping() {
    delete[] _texCoords;
  }
  
  int getTextureId() const {
    return _textureId;
  }
  
  const float* getTexCoords() const {
    return _texCoords;
  }
  
  void bind(const RenderContext* rc) const;  
};

#endif
