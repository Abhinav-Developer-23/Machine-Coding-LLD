# S3 Module Architecture

This document contains a structured overview and interactive UML diagrams for the **S3** simplified object storage system module.

## Core Components
- **`S3Service`**: The primary API gateway orchestrating bucket creation, deletion, file management, and ACL configurations.
- **`AuthorizationService`**: Decouples auth checks from business logic, querying the configured policy strategy.
- **`AuthorizationStrategy`**: Contains concrete auth verification logic.
- **`Acl`**: Models access control lists using a map of users to sets of permissions.
- **`Bucket` & `S3Object`**: Core entities owning an `Acl`.

---

## 1. Class Diagram (Mermaid)

Below is the class diagram illustrating classes, variables, and relations.

```mermaid
classDiagram
    direction TB

    class S3Demo {
        +main(args: String[])$
    }

    class S3Service {
        -BucketRepository bucketRepository
        -S3ObjectRepository objectRepository
        -AuthorizationService authorizationService
        +createBucket(actor, name)
        +deleteBucket(actor, name)
        +uploadFile(actor, bucket, key, content, metadata) S3Object
        +readFile(actor, bucket, key) String
        +updateFile(actor, bucket, key, content, metadata)
        +deleteFile(actor, bucket, key)
        +listFiles(actor, bucket) List~String~
        +grantBucketPermission(actor, bucket, target, permission)
        +revokeBucketPermission(actor, bucket, target, permission)
        +grantFilePermission(actor, bucket, key, target, permission)
        +revokeFilePermission(actor, bucket, key, target, permission)
    }

    class AuthorizationService {
        -AuthorizationStrategy authorizationStrategy
        +canAccessBucket(user, bucket, permission) bool
        +canAccessObject(user, bucket, object, permission) bool
        +requireBucketOwner(user, bucket)
        +requireObjectOwnerOrBucketOwner(user, bucket, object)
        +requireBucketAccess(user, bucket, permission)
        +requireObjectAccess(user, bucket, object, permission)
    }

    class AuthorizationStrategy {
        +canAccessBucket(user, bucket, permission) bool
        +canAccessObject(user, bucket, object, permission) bool
    }

    class Bucket {
        -String name
        -String ownerUserId
        -Acl acl
        +getName() String
        +getOwnerUserId() String
        +getAcl() Acl
    }

    class S3Object {
        -String bucketName
        -String key
        -String content
        -Map~String, String~ metadata
        -String ownerUserId
        -Acl acl
        +getBucketName() String
        +getKey() String
        +getContent() String
        +updateContent(content, metadata)
        +getAcl() Acl
    }

    class Acl {
        -Map~String, Set~Permission~~ userPermissions
        +grant(userId, permission)
        +revoke(userId, permission)
        +hasEntryFor(userId) bool
        +allows(userId, permission) bool
        +snapshot() Map~String, Set~Permission~~
    }

    class BucketRepository {
        -BucketRepository instance$
        -Map~String, Bucket~ buckets
        +getInstance() BucketRepository$
        +save(bucket)
        +findByName(name) Bucket
        +delete(name)
        +findAll() List~Bucket~
    }

    class S3ObjectRepository {
        -S3ObjectRepository instance$
        -Map~String, Map~String, S3Object~~ objects
        +getInstance() S3ObjectRepository$
        +save(object) S3Object
        +findByBucketAndKey(bucket, key) S3Object
        +findByBucket(bucket) List~S3Object~
        +delete(bucket, key)
        +deleteByBucket(bucket)
    }

    class Permission {
        <<enumeration>>
        READ
        WRITE
    }

    S3Demo --> S3Service : invokes
    S3Service --> BucketRepository : uses
    S3Service --> S3ObjectRepository : uses
    S3Service --> AuthorizationService : uses
    AuthorizationService --> AuthorizationStrategy : delegates
    Bucket --> Acl : owns
    S3Object --> Acl : owns
    Acl --> Permission : uses
```

---

## 2. Dynamic Interaction Sequence (Mermaid)

The sequence diagram below visualizes the execution flow of **`updateFile`** verifying permissions, showing the interaction between the service, authorization strategy, and ACL.

```mermaid
sequenceDiagram
    autonumber
    actor Actor as Charlie
    participant Service as S3Service
    participant Auth as AuthorizationService
    participant Strategy as AuthorizationStrategy
    participant Obj as S3Object
    participant ObjAcl as Object Acl
    participant BktAcl as Bucket Acl

    Actor->>Service: updateFile(Charlie, "docs", "design.txt", "v3 design", metadata)
    Service->>Auth: requireObjectAccess(Charlie, bucket, object, WRITE)
    Auth->>Strategy: canAccessObject(Charlie, bucket, object, WRITE)
    
    rect rgb(240, 248, 255)
        Note over Strategy: Check Ownership
        Strategy-->>Strategy: isOwner(Charlie)? -> False
    end

    Strategy->>Obj: getAcl()
    Obj-->>Strategy: objectAcl
    Strategy->>ObjAcl: hasEntryFor(Charlie)
    
    alt Object ACL has entry for user
        ObjAcl-->>Strategy: true
        Strategy->>ObjAcl: allows(Charlie, WRITE)
        ObjAcl-->>Strategy: result (true/false)
    else Object ACL has no entry
        ObjAcl-->>Strategy: false
        Strategy->>BktAcl: allows(Charlie, WRITE)
        BktAcl-->>Strategy: result (true/false)
    end

    Strategy-->>Auth: canAccess (e.g. false)
    
    alt Access Denied
        Auth-->>Service: throw SecurityException("Access Denied")
        Service-->>Actor: propagates SecurityException
    else Access Allowed
        Auth-->>Service: success
        Service->>Obj: updateContent("v3 design", metadata)
        Service-->>Actor: void
    end
```
