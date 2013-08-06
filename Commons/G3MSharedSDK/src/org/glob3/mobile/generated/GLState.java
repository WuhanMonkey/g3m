package org.glob3.mobile.generated; 
//
//  GLState.cpp
//  G3MiOSSDK
//
//  Created by Jose Miguel SN on 17/05/13.
//
//

//
//  GLState.h
//  G3MiOSSDK
//
//  Created by Jose Miguel SN on 17/05/13.
//
//  Created by Agustin Trujillo Pino on 27/10/12.
//  Copyright (c) 2012 Universidad de Las Palmas. All rights reserved.
//





public class GLState
{

  private GLFeatureSet _features = new GLFeatureSet();
  private GLFeatureSet _accumulatedFeatures;

  private int _timeStamp;
  private int _parentsTimeStamp;

  private GPUVariableValueSet _valuesSet;
  private GLGlobalState _globalState;

  private GPUProgram _lastGPUProgramUsed;

  private GLState _parentGLState;

//C++ TO JAVA CONVERTER TODO TASK: The implementation of the following method could not be found:
//  GLState(GLState state);

  private void hasChangedStructure()
  {
    _timeStamp++;
    if (_valuesSet != null)
       _valuesSet.dispose();
    _valuesSet = null;
    if (_globalState != null)
       _globalState.dispose();
    _globalState = null;
    _lastGPUProgramUsed = null;
  
    if (_accumulatedFeatures != null)
       _accumulatedFeatures.dispose();
    _accumulatedFeatures = null;
  }


  public GLState()
  {
     _parentGLState = null;
     _lastGPUProgramUsed = null;
     _parentsTimeStamp = 0;
     _timeStamp = 0;
     _valuesSet = null;
     _globalState = null;
     _accumulatedFeatures = null;
  }

  public final int getTimeStamp()
  {
     return _timeStamp;
  }

  public final GLFeatureSet getAccumulatedFeatures()
  {
    if (_accumulatedFeatures == null)
    {
  
      _accumulatedFeatures = new GLFeatureSet();
  
      if (_parentGLState != null)
      {
        GLFeatureSet parents = _parentGLState.getAccumulatedFeatures();
        if (parents != null)
        {
          _accumulatedFeatures.add(parents);
        }
      }
      _accumulatedFeatures.add(_features);
  
    }
    return _accumulatedFeatures;
  }
//  GLFeatureSet* createAccumulatedFeatures() const;

  public void dispose()
  {
    if (_accumulatedFeatures != null)
       _accumulatedFeatures.dispose();
  
    if (_valuesSet != null)
       _valuesSet.dispose();
    if (_globalState != null)
       _globalState.dispose();
  
  //  printf("TIMESTAMP: %d\n", _timeStamp);
  }

  public final void setParent(GLState parent)
  {
  
    if (parent == null)
    {
      if (parent != _parentGLState)
      {
        _parentGLState = null;
        _parentsTimeStamp = -1;
        hasChangedStructure();
      }
    }
    else
    {
      final int parentsTimeStamp = parent.getTimeStamp();
      if ((parent != _parentGLState) || (_parentsTimeStamp != parentsTimeStamp))
      {
        _parentGLState = parent;
        _parentsTimeStamp = parentsTimeStamp;
        hasChangedStructure();
      }
    }
  }

  public final void applyOnGPU(GL gl, GPUProgramManager progManager)
  {
  
  
    if (_valuesSet == null && _globalState == null)
    {
  
      _valuesSet = new GPUVariableValueSet();
      _globalState = new GLGlobalState();
  
      GLFeatureSet accumulatedFeatures = getAccumulatedFeatures();
  //    GLFeatureSet* accumulatedFeatures = createAccumulatedFeatures();
  
      for (int i = 0; i < DefineConstants.N_GLFEATURES_GROUPS; i++)
      {
        GLFeatureGroupName groupName = GLFeatureGroup.getGroupName(i);
        GLFeatureGroup group = GLFeatureGroup.createGroup(groupName);
  
        for (int j = 0; j < accumulatedFeatures.size(); j++)
        {
          final GLFeature f = accumulatedFeatures.get(j);
          if (f.getGroup() == groupName)
          {
            group.add(f);
          }
        }
        group.addToGPUVariableSet(_valuesSet);
        group.applyOnGlobalGLState(_globalState);
  
        if (group != null)
           group.dispose();
      }
  
  //    delete accumulatedFeatures;
  
      final int uniformsCode = _valuesSet.getUniformsCode();
      final int attributesCode = _valuesSet.getAttributesCode();
  
      _lastGPUProgramUsed = progManager.getProgram(gl, uniformsCode, attributesCode);
    }
  
    if (_valuesSet == null || _globalState == null)
    {
      ILogger.instance().logError("GLState logic error.");
      return;
    }
  
    if (_lastGPUProgramUsed != null)
    {
      gl.useProgram(_lastGPUProgramUsed);
  
      _valuesSet.applyValuesToProgram(_lastGPUProgramUsed);
      _globalState.applyChanges(gl, gl.getCurrentGLGlobalState());
  
      _lastGPUProgramUsed.applyChanges(gl);
  
      //prog->onUnused(); //Uncomment to check that all GPUProgramStates are complete
    }
    else
    {
      ILogger.instance().logError("No GPUProgram found.");
    }
  
  }


  //GLFeatureSet* GLState::createAccumulatedFeatures() const{
  //  GLFeatureSet* accumulatedFeatures = new GLFeatureSet();
  //
  //  if (_parentGLState != NULL){
  //    GLFeatureSet* parents = _parentGLState->createAccumulatedFeatures();
  //    if (parents != NULL){
  //      accumulatedFeatures->add(parents);
  //    }
  //    delete parents;
  //  }
  //  accumulatedFeatures->add(&_features);
  //  return accumulatedFeatures;
  //}
  
  public final void addGLFeature(GLFeature f, boolean mustRetain)
  {
    _features.add(f);
  
    if (!mustRetain)
    {
      f._release();
    }
  
    hasChangedStructure();
  }

  public final void clearGLFeatureGroup(GLFeatureGroupName g)
  {
    _features.clearFeatures(g);
    hasChangedStructure();
  }

}