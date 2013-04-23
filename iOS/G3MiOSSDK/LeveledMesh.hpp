//
//  LeveledMesh.h
//  G3MiOSSDK
//
//  Created by Jose Miguel SN on 16/04/13.
//
//

#ifndef __G3MiOSSDK__LeveledMesh__
#define __G3MiOSSDK__LeveledMesh__

#include <iostream>

#include "Mesh.hpp"
#include "Vector3D.hpp"

class LeveledMesh: public Mesh{
private:
  Mesh* _mesh;
  
  int _currentLevel;
  
public:
  LeveledMesh(Mesh* mesh, int level) :
  _mesh(mesh),
  _currentLevel(level)
  {
    
  }
  
  void setMesh(Mesh* mesh, int level) {
    if (_mesh != mesh && level >= _currentLevel) {
      delete _mesh;
      _mesh = mesh;
      _currentLevel = level;
    }
  }
  
  ~LeveledMesh() {
    delete _mesh;
  }
  
  int getVertexCount() const {
    return _mesh->getVertexCount();
  }
  
  const Vector3D getVertex(int i) const {
    return _mesh->getVertex(i);
  }
  
  void render(const G3MRenderContext* rc,
              const GLState& parentState) const {
    _mesh->render(rc, parentState);
  }
  
  Extent* getExtent() const {
    return _mesh->getExtent();
  }
  
  bool isTransparent(const G3MRenderContext* rc) const {
    return _mesh->isTransparent(rc);
  }
  
  int getLevel() const{
    return _currentLevel;
  }
  
};

#endif /* defined(__G3MiOSSDK__LeveledMesh__) */
