//
//  ShapeScaleEffect.cpp
//  G3MiOSSDK
//
//  Created by Diego Gomez Deck on 11/23/12.
//
//

#include "ShapeScaleEffect.hpp"

#include "IMathUtils.hpp"
#include "Shape.hpp"

void ShapeScaleEffect::doStep(const G3MRenderContext *rc,
                              const TimeInterval& when) {
  const double alpha = pace( percentDone(when) );

  IMathUtils* mu = IMathUtils::instance();
  const double scaleX = mu->interpolation(_fromScaleX, _toScaleX, alpha);
  const double scaleY = mu->interpolation(_fromScaleY, _toScaleY, alpha);
  const double scaleZ = mu->interpolation(_fromScaleZ, _toScaleZ, alpha);

  _shape->setScale(scaleX, scaleY, scaleZ);
}

void ShapeScaleEffect::cancel(const TimeInterval& when) {
  _shape->setScale(_toScaleX, _toScaleY, _toScaleZ);
}

void ShapeScaleEffect::stop(const G3MRenderContext *rc,
                            const TimeInterval& when) {
  _shape->setScale(_toScaleX, _toScaleY, _toScaleZ);
}
