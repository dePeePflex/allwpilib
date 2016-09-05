/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2016. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

#ifndef CAMERASERVER_UNLIMITEDHANDLERESOURCE_H_
#define CAMERASERVER_UNLIMITEDHANDLERESOURCE_H_

#include <memory>
#include <mutex>
#include <vector>

#include "llvm/SmallVector.h"
#include "support/atomic_static.h"

namespace cs {

// The UnlimitedHandleResource class is a way to track handles. This version
// allows an unlimted number of handles that are allocated sequentially. When
// possible, indices are reused to save memory usage and keep the array length
// down.
// However, automatic array management has not been implemented, but might be in
// the future.
// Because we have to loop through the allocator, we must use a global mutex.
//
// THandle needs to have the following attributes:
//  Type : enum or typedef
//  kIndexMax : static, constexpr, or enum value for the maximum index value
//  int GetTypedIndex() const : function that returns the index of the handle
//  THandle(int index, HandleType[int] type) : constructor for index and type
//
// @tparam THandle The Handle Type
// @tparam TStruct The struct type held by this resource
// @tparam typeValue The type value stored in the handle
// @tparam TMutex The mutex type to use
template <typename THandle, typename TStruct, int typeValue,
          typename TMutex = std::mutex>
class UnlimitedHandleResource {
 public:
  UnlimitedHandleResource(const UnlimitedHandleResource&) = delete;
  UnlimitedHandleResource operator=(const UnlimitedHandleResource&) = delete;
  UnlimitedHandleResource() = default;

  template <typename... Args>
  THandle Allocate(Args&&... args);
  THandle Allocate(std::shared_ptr<THandle> structure);

  std::shared_ptr<TStruct> Get(THandle handle);

  void Free(THandle handle);

  template <typename T>
  void GetAll(llvm::SmallVectorImpl<T>& handles);

 private:
  THandle MakeHandle(size_t i) {
    return THandle{static_cast<int>(i),
                   static_cast<typename THandle::Type>(typeValue)};
  }
  std::vector<std::shared_ptr<TStruct>> m_structures;
  TMutex m_handleMutex;
};

template <typename THandle, typename TStruct, int typeValue, typename TMutex>
template <typename... Args>
THandle UnlimitedHandleResource<THandle, TStruct, typeValue, TMutex>::Allocate(
    Args&&... args) {
  std::lock_guard<TMutex> sync(m_handleMutex);
  size_t i;
  for (i = 0; i < m_structures.size(); i++) {
    if (m_structures[i] == nullptr) {
      m_structures[i] = std::make_shared<TStruct>(std::forward<Args>(args)...);
      return MakeHandle(i);
    }
  }
  if (i >= THandle::kIndexMax) return 0;

  m_structures.emplace_back(
      std::make_shared<TStruct>(std::forward<Args>(args)...));
  return MakeHandle(i);
}

template <typename THandle, typename TStruct, int typeValue, typename TMutex>
THandle UnlimitedHandleResource<THandle, TStruct, typeValue, TMutex>::Allocate(
    std::shared_ptr<THandle> structure) {
  std::lock_guard<TMutex> sync(m_handleMutex);
  size_t i;
  for (i = 0; i < m_structures.size(); i++) {
    if (m_structures[i] == nullptr) {
      m_structures[i] = structure;
      return MakeHandle(i);
    }
  }
  if (i >= THandle::kIndexMax) return 0;

  m_structures.push_back(structure);
  return MakeHandle(i);
}

template <typename THandle, typename TStruct, int typeValue, typename TMutex>
std::shared_ptr<TStruct> UnlimitedHandleResource<THandle, TStruct, typeValue,
                                                 TMutex>::Get(THandle handle) {
  auto index =
      handle.GetTypedIndex(static_cast<typename THandle::Type>(typeValue));
  if (index < 0) return nullptr;
  std::lock_guard<TMutex> sync(m_handleMutex);
  if (index >= static_cast<int>(m_structures.size()))
    return nullptr;
  return m_structures[index];
}

template <typename THandle, typename TStruct, int typeValue, typename TMutex>
void UnlimitedHandleResource<THandle, TStruct, typeValue, TMutex>::Free(
    THandle handle) {
  auto index =
      handle.GetTypedIndex(static_cast<typename THandle::Type>(typeValue));
  if (index < 0) return;
  std::lock_guard<TMutex> sync(m_handleMutex);
  if (index >= static_cast<int>(m_structures.size())) return;
  m_structures[index].reset();
}

template <typename THandle, typename TStruct, int typeValue, typename TMutex>
template <typename T>
void UnlimitedHandleResource<THandle, TStruct, typeValue, TMutex>::GetAll(
    llvm::SmallVectorImpl<T>& handles) {
  std::lock_guard<TMutex> sync(m_handleMutex);
  size_t i;
  for (i = 0; i < m_structures.size(); i++) {
    if (m_structures[i] != nullptr) handles.push_back(MakeHandle(i));
  }
}

template <typename THandle, typename TStruct, int typeValue,
          typename TMutex = std::mutex>
class StaticUnlimitedHandleResource
    : public UnlimitedHandleResource<THandle, TStruct, typeValue, TMutex> {
 public:
  static StaticUnlimitedHandleResource& GetInstance() {
    ATOMIC_STATIC(StaticUnlimitedHandleResource, instance);
    return instance;
  }

 private:
  StaticUnlimitedHandleResource() {}

  ATOMIC_STATIC_DECL(StaticUnlimitedHandleResource)
};

}  // namespace cs

#endif  // CAMERASERVER_UNLIMITEDHANDLERESOURCE_H_
