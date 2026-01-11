# Spring Transaction

Spring Transaction est le module spécifique chargé de l’intégration des transactions. Il offre plusieurs avantages :

- Il fournit une abstraction au dessus des différentes solutions disponibles dans le monde Java pour les gestion des transactions. Spring Transaction définit l’interface ``TransactionManager`` pour unifier ses différentes solutions et propose des classes concrètes pour chacun d’entre-elles.

- Il se base sur la programmation orientée aspect (AOP) pour gérer la démarcation transactionnelles dans le code de notre application.

- Il permet une gestion déclarative des transactions.

## La transaction

La notion de transaction est récurrente dans les systèmes d’information. Par exemple, la plupart des SGBDR (Oracle, MySQL, PostGreSQL…) **intègrent un moteur de transaction**. Une transaction est définie par le respect de quatre propriétés désignées par l’acronyme ACID :

**Atomicité**

  La transaction garantit que l’ensemble des opérations qui la composent sont soit toutes réalisées avec succès soit aucune n’est conservée.

**Cohérence**

  La transaction garantit qu’elle fait passer le système d’un état valide vers un autre état valide.

**Isolation**

  Deux transactions sont isolées l’une de l’autre. C’est-à-dire que leur exécution simultanée produit le même résultat que si elles avaient été exécutées successivement.

**Durabilité**

  La transaction garantit qu’après son exécution, les modifications qu’elle a apportées au système sont conservées durablement.

Une transaction est définie par un début et une fin qui peut être soit une validation des modifications (commit), soit une annulation des modifications effectuées (rollback). On parle de **démarcation transactionnelle** pour désigner la portion de code qui doit s’exécuter dans le cadre d’une transaction.

## Spring Boot et la configuration automatique

La plupart des applications qui interagissent avec un SGBDR n’incorporent pas de moteur de gestion de transactions. Elles se contentent de déléguer cette gestion au moteur interne du SGBDR.

Néanmoins, il existe des systèmes d’information qui supportent les transactions. La gestion des transactions n’est donc que partiellement liée aux systèmes de bases de données. C’est pour cette raison qu’il existe un standard Java dédié à la gestion des transactions : ``JTA`` (*Java Transaction API*). 

Il s’agit de l’API officielle pour interagir avec un moteur transactionnel. Cependant, cette API n’est pas systématiquement utilisée et il existe des solutions fournies par des technologies particulières. Par exemple, JDBC et JPA fournissent toutes deux leur propre solution et leur propre API pour gérer les transactions impliquant spécifiquement des bases de données. Il existe donc plusieurs solutions pour implémenter la gestion des transactions en Java.

Heureusement, Spring Boot joue pleinement son rôle en rendant, la plupart du temps, la configuration de la gestion des transactions complètement transparente. Spring Boot va se baser sur les dépendances déclarées dans le projet pour savoir quel gestionnaire de transaction (``TransactionManager``) doit être créé dans le contexte de votre application.

Si votre projet est géré par Maven et que vous avez ajouté une dépendance à :

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jdbc</artifactId>
</dependency>
```

Alors, c’est un bean de type ``JdbcTransactionManager`` qui sera ajouté dans votre contexte d’application et c’est l’API JDBC qui sera utilisée pour gérer les transactions.

Si vous avez ajouté une dépendance à :

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

Alors, c’est un bean de type ``JpaTransactionManager`` qui sera ajouté dans votre contexte d’application et c’est l’API JPA qui sera utilisée pour gérer les transactions.

Pour activer le support des transactions gérées géré par ``JTA``, il faut ajouter un moteur transactionnel comme Atomikos. Cela se fait toujours par la déclaration d’une dépendance :

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-jta-atomikos</artifactId>
</dependency>
```

## Transaction et couche de service

*La démarcation transactionnelle* désigne la portion de code au début de laquelle une transaction doit être commencée et à la fin de laquelle la transaction doit être validée (commit) ou annulée (rollback).
Dans une approche objet, la méthode est une bonne unité pour déclarer la démarcation d’une transaction : la transaction est démarrée à l’appel de la méthode et elle est validée (ou annulée) au retour de la méthode.

Dans une architecture multi-couches (N-tiers), on identifie généralement une couche de service, appelée aussi couche métier (business tier). Comme une méthode d’une classe de service représente la réalisation d’une fonctionnalité de l’application, il est assez évident que les méthodes de service sont de bonnes candidates pour fournir une démarcation transactionnelle.

> [!WARNING]
> Pour des raisons discutables, Spring Data JPA choisit d’activer par défaut les transactions sur les méthodes des repositories. Cela signifie que les transactions fonctionnent par défaut mais qu’elles sont validées par les méthodes des repositories, c’est-à-dire dans la couche d’accès aux données. Cela peut entraîner des incohérences de données. Par exemple, la méthode d’un service appelle plusieurs méthodes de repositories pour réaliser une fonctionnalité. Si un problème survient au cours de l’exécution de la méthode de service alors les appels déjà effectués aux repositories ne pourront pas être annulés.
>
> À part pour des applications très simples, la démarcation transactionnelle au niveau de la couche d’accès aux données est systématiquement une mauvaise idée. Si vous utilisez Spring Data JPA, vous devriez désactiver la prise en charge automatique des transactions par les repositories avec l’annotation ``@EnableJpaRepositories``
>
> 
```java
@SpringBootApplication
@EnableJpaRepositories(enableDefaultTransactions = false)
public class MyApplication {

  public static void main(String[] args) {
    SpringApplication.run(MyApplication.class, args);
  }

}
```
>
> Une fois cette option désactivée, cela signifie qu’un appel à une méthode de repository qui effectue une modification sur la base de données devra être appelée dans le cadre d’une transaction ou sinon l’appel échouera.