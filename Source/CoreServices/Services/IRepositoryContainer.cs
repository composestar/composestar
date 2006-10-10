using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.Repository
{

    /// <summary>
    /// Interface for the Repository Container
    /// </summary>
    public interface IRepositoryContainer
    {
        /// <summary>
        /// Gets or sets a value indicating whether [debug mode].
        /// </summary>
        /// <value><c>true</c> if [debug mode]; otherwise, <c>false</c>.</value>
        bool DebugMode {get; set;}

        /// <summary>
        /// Opens the container.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        void OpenContainer(String fileName);

        /// <summary>
        /// Closes the container.
        /// </summary>
        void CloseContainer();

        /// <summary>
        /// Deletes the container.
        /// </summary>
        void DeleteContainer();

        /// <summary>
        /// Stores the object.
        /// </summary>
        /// <param name="objectToStore">The object to store.</param>
        void StoreObject(Object objectToStore);

        /// <summary>
        /// Gets the objects.
        /// </summary>
        /// <returns></returns>
        IList<T> GetObjects<T>();

        /// <summary>
        /// Gets the object query.
        /// </summary>
        /// <param name="match">The match.</param>
        /// <returns></returns>
        IList<T> GetObjectQuery<T>(Predicate<T> match);

        /// <summary>
        /// Gets the object based on a predicate.
        /// </summary>
        /// <param name="match">The match.</param>
        /// <returns></returns>
        T GetObject<T>(Predicate<T> match);

        /// <summary>
        /// Deletes the objects.
        /// </summary>
        void DeleteObjects<T>();

        /// <summary>
        /// Deletes the objects.
        /// </summary>
        /// <param name="match">The match.</param>
        void DeleteObjects<T>(Predicate<T> match);

        /// <summary>
        /// Commits this instance.
        /// </summary>
        void Commit();
    }
}
